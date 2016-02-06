begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.facet.taxonomy
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|FacetsConfig
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|FacetsConfig
operator|.
name|DimConfig
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|OrdinalsReader
operator|.
name|OrdinalsSegmentReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|directory
operator|.
name|DirectoryTaxonomyWriter
operator|.
name|OrdinalMap
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|FilterLeafReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|BinaryDocValues
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IntsRef
import|;
end_import
begin_comment
comment|/**  * A {@link org.apache.lucene.index.FilterLeafReader} for updating facets ordinal references,  * based on an ordinal map. You should use this code in conjunction with merging  * taxonomies - after you merge taxonomies, you receive an {@link OrdinalMap}  * which maps the 'old' ordinals to the 'new' ones. You can use that map to  * re-map the doc values which contain the facets information (ordinals) either  * before or while merging the indexes.  *<p>  * For re-mapping the ordinals during index merge, do the following:  *   *<pre class="prettyprint">  * // merge the old taxonomy with the new one.  * OrdinalMap map = new MemoryOrdinalMap();  * DirectoryTaxonomyWriter.addTaxonomy(srcTaxoDir, map);  * int[] ordmap = map.getMap();  *   * // Add the index and re-map ordinals on the go  * DirectoryReader reader = DirectoryReader.open(oldDir);  * IndexWriterConfig conf = new IndexWriterConfig(VER, ANALYZER);  * IndexWriter writer = new IndexWriter(newDir, conf);  * List&lt;LeafReaderContext&gt; leaves = reader.leaves();  * LeafReader wrappedLeaves[] = new LeafReader[leaves.size()];  * for (int i = 0; i&lt; leaves.size(); i++) {  *   wrappedLeaves[i] = new OrdinalMappingLeafReader(leaves.get(i).reader(), ordmap);  * }  * writer.addIndexes(new MultiReader(wrappedLeaves));  * writer.commit();  *</pre>  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|OrdinalMappingLeafReader
specifier|public
class|class
name|OrdinalMappingLeafReader
extends|extends
name|FilterLeafReader
block|{
comment|// silly way, but we need to use dedupAndEncode and it's protected on FacetsConfig.
DECL|class|InnerFacetsConfig
specifier|private
specifier|static
class|class
name|InnerFacetsConfig
extends|extends
name|FacetsConfig
block|{
DECL|method|InnerFacetsConfig
name|InnerFacetsConfig
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|dedupAndEncode
specifier|public
name|BytesRef
name|dedupAndEncode
parameter_list|(
name|IntsRef
name|ordinals
parameter_list|)
block|{
return|return
name|super
operator|.
name|dedupAndEncode
argument_list|(
name|ordinals
argument_list|)
return|;
block|}
block|}
DECL|class|OrdinalMappingBinaryDocValues
specifier|private
class|class
name|OrdinalMappingBinaryDocValues
extends|extends
name|BinaryDocValues
block|{
DECL|field|ordinals
specifier|private
specifier|final
name|IntsRef
name|ordinals
init|=
operator|new
name|IntsRef
argument_list|(
literal|32
argument_list|)
decl_stmt|;
DECL|field|ordsReader
specifier|private
specifier|final
name|OrdinalsSegmentReader
name|ordsReader
decl_stmt|;
DECL|method|OrdinalMappingBinaryDocValues
name|OrdinalMappingBinaryDocValues
parameter_list|(
name|OrdinalsSegmentReader
name|ordsReader
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|ordsReader
operator|=
name|ordsReader
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"synthetic-access"
argument_list|)
annotation|@
name|Override
DECL|method|get
specifier|public
name|BytesRef
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
try|try
block|{
comment|// NOTE: this isn't quite koscher, because in general
comment|// multiple threads can call BinaryDV.get which would
comment|// then conflict on the single ordinals instance, but
comment|// because this impl is only used for merging, we know
comment|// only 1 thread calls us:
name|ordsReader
operator|.
name|get
argument_list|(
name|docID
argument_list|,
name|ordinals
argument_list|)
expr_stmt|;
comment|// map the ordinals
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ordinals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ordinals
operator|.
name|ints
index|[
name|i
index|]
operator|=
name|ordinalMap
index|[
name|ordinals
operator|.
name|ints
index|[
name|i
index|]
index|]
expr_stmt|;
block|}
return|return
name|encode
argument_list|(
name|ordinals
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"error reading category ordinals for doc "
operator|+
name|docID
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|field|ordinalMap
specifier|private
specifier|final
name|int
index|[]
name|ordinalMap
decl_stmt|;
DECL|field|facetsConfig
specifier|private
specifier|final
name|InnerFacetsConfig
name|facetsConfig
decl_stmt|;
DECL|field|facetFields
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|facetFields
decl_stmt|;
comment|/**    * Wraps an LeafReader, mapping ordinals according to the ordinalMap, using    * the provided {@link FacetsConfig} which was used to build the wrapped    * reader.    */
DECL|method|OrdinalMappingLeafReader
specifier|public
name|OrdinalMappingLeafReader
parameter_list|(
name|LeafReader
name|in
parameter_list|,
name|int
index|[]
name|ordinalMap
parameter_list|,
name|FacetsConfig
name|srcConfig
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|ordinalMap
operator|=
name|ordinalMap
expr_stmt|;
name|facetsConfig
operator|=
operator|new
name|InnerFacetsConfig
argument_list|()
expr_stmt|;
name|facetFields
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|DimConfig
name|dc
range|:
name|srcConfig
operator|.
name|getDimConfigs
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|facetFields
operator|.
name|add
argument_list|(
name|dc
operator|.
name|indexFieldName
argument_list|)
expr_stmt|;
block|}
comment|// always add the default indexFieldName. This is because FacetsConfig does
comment|// not explicitly record dimensions that were indexed under the default
comment|// DimConfig, unless they have a custome DimConfig.
name|facetFields
operator|.
name|add
argument_list|(
name|FacetsConfig
operator|.
name|DEFAULT_DIM_CONFIG
operator|.
name|indexFieldName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: encodes category ordinals into a BytesRef. Override in case you use    * custom encoding, other than the default done by FacetsConfig.    */
DECL|method|encode
specifier|protected
name|BytesRef
name|encode
parameter_list|(
name|IntsRef
name|ordinals
parameter_list|)
block|{
return|return
name|facetsConfig
operator|.
name|dedupAndEncode
argument_list|(
name|ordinals
argument_list|)
return|;
block|}
comment|/**    * Expert: override in case you used custom encoding for the categories under    * this field.    */
DECL|method|getOrdinalsReader
specifier|protected
name|OrdinalsReader
name|getOrdinalsReader
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
operator|new
name|DocValuesOrdinalsReader
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBinaryDocValues
specifier|public
name|BinaryDocValues
name|getBinaryDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|facetFields
operator|.
name|contains
argument_list|(
name|field
argument_list|)
condition|)
block|{
specifier|final
name|OrdinalsReader
name|ordsReader
init|=
name|getOrdinalsReader
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
operator|new
name|OrdinalMappingBinaryDocValues
argument_list|(
name|ordsReader
operator|.
name|getReader
argument_list|(
name|in
operator|.
name|getContext
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|in
operator|.
name|getBinaryDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
