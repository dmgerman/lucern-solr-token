begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Useful constants representing filenames and extensions used by lucene  *   * @author Bernhard Messer  * @version $rcs = ' $Id: Exp $ ' ;  */
end_comment
begin_class
DECL|class|IndexFileNames
specifier|final
class|class
name|IndexFileNames
block|{
comment|/** Name of the index segment file */
DECL|field|SEGMENTS
specifier|static
specifier|final
name|String
name|SEGMENTS
init|=
literal|"segments"
decl_stmt|;
comment|/** Name of the generation reference file name */
DECL|field|SEGMENTS_GEN
specifier|static
specifier|final
name|String
name|SEGMENTS_GEN
init|=
literal|"segments.gen"
decl_stmt|;
comment|/** Name of the index deletable file (only used in    * pre-lockless indices) */
DECL|field|DELETABLE
specifier|static
specifier|final
name|String
name|DELETABLE
init|=
literal|"deletable"
decl_stmt|;
comment|/** Extension of norms file */
DECL|field|NORMS_EXTENSION
specifier|static
specifier|final
name|String
name|NORMS_EXTENSION
init|=
literal|"nrm"
decl_stmt|;
comment|/** Extension of freq postings file */
DECL|field|FREQ_EXTENSION
specifier|static
specifier|final
name|String
name|FREQ_EXTENSION
init|=
literal|"frq"
decl_stmt|;
comment|/** Extension of prox postings file */
DECL|field|PROX_EXTENSION
specifier|static
specifier|final
name|String
name|PROX_EXTENSION
init|=
literal|"prx"
decl_stmt|;
comment|/** Extension of terms file */
DECL|field|TERMS_EXTENSION
specifier|static
specifier|final
name|String
name|TERMS_EXTENSION
init|=
literal|"tis"
decl_stmt|;
comment|/** Extension of terms index file */
DECL|field|TERMS_INDEX_EXTENSION
specifier|static
specifier|final
name|String
name|TERMS_INDEX_EXTENSION
init|=
literal|"tii"
decl_stmt|;
comment|/** Extension of stored fields index file */
DECL|field|FIELDS_INDEX_EXTENSION
specifier|static
specifier|final
name|String
name|FIELDS_INDEX_EXTENSION
init|=
literal|"fdx"
decl_stmt|;
comment|/** Extension of stored fields file */
DECL|field|FIELDS_EXTENSION
specifier|static
specifier|final
name|String
name|FIELDS_EXTENSION
init|=
literal|"fdt"
decl_stmt|;
comment|/** Extension of vectors fields file */
DECL|field|VECTORS_FIELDS_EXTENSION
specifier|static
specifier|final
name|String
name|VECTORS_FIELDS_EXTENSION
init|=
literal|"tvf"
decl_stmt|;
comment|/** Extension of vectors documents file */
DECL|field|VECTORS_DOCUMENTS_EXTENSION
specifier|static
specifier|final
name|String
name|VECTORS_DOCUMENTS_EXTENSION
init|=
literal|"tvd"
decl_stmt|;
comment|/** Extension of vectors index file */
DECL|field|VECTORS_INDEX_EXTENSION
specifier|static
specifier|final
name|String
name|VECTORS_INDEX_EXTENSION
init|=
literal|"tvx"
decl_stmt|;
comment|/** Extension of compound file */
DECL|field|COMPOUND_FILE_EXTENSION
specifier|static
specifier|final
name|String
name|COMPOUND_FILE_EXTENSION
init|=
literal|"cfs"
decl_stmt|;
comment|/** Extension of compound file for doc store files*/
DECL|field|COMPOUND_FILE_STORE_EXTENSION
specifier|static
specifier|final
name|String
name|COMPOUND_FILE_STORE_EXTENSION
init|=
literal|"cfx"
decl_stmt|;
comment|/** Extension of deletes */
DECL|field|DELETES_EXTENSION
specifier|static
specifier|final
name|String
name|DELETES_EXTENSION
init|=
literal|"del"
decl_stmt|;
comment|/** Extension of field infos */
DECL|field|FIELD_INFOS_EXTENSION
specifier|static
specifier|final
name|String
name|FIELD_INFOS_EXTENSION
init|=
literal|"fnm"
decl_stmt|;
comment|/** Extension of plain norms */
DECL|field|PLAIN_NORMS_EXTENSION
specifier|static
specifier|final
name|String
name|PLAIN_NORMS_EXTENSION
init|=
literal|"f"
decl_stmt|;
comment|/** Extension of separate norms */
DECL|field|SEPARATE_NORMS_EXTENSION
specifier|static
specifier|final
name|String
name|SEPARATE_NORMS_EXTENSION
init|=
literal|"s"
decl_stmt|;
comment|/** Extension of gen file */
DECL|field|GEN_EXTENSION
specifier|static
specifier|final
name|String
name|GEN_EXTENSION
init|=
literal|"gen"
decl_stmt|;
comment|/**    * This array contains all filename extensions used by    * Lucene's index files, with two exceptions, namely the    * extension made up from<code>.f</code> + a number and    * from<code>.s</code> + a number.  Also note that    * Lucene's<code>segments_N</code> files do not have any    * filename extension.    */
DECL|field|INDEX_EXTENSIONS
specifier|static
specifier|final
name|String
name|INDEX_EXTENSIONS
index|[]
init|=
operator|new
name|String
index|[]
block|{
name|COMPOUND_FILE_EXTENSION
block|,
name|FIELD_INFOS_EXTENSION
block|,
name|FIELDS_INDEX_EXTENSION
block|,
name|FIELDS_EXTENSION
block|,
name|TERMS_INDEX_EXTENSION
block|,
name|TERMS_EXTENSION
block|,
name|FREQ_EXTENSION
block|,
name|PROX_EXTENSION
block|,
name|DELETES_EXTENSION
block|,
name|VECTORS_INDEX_EXTENSION
block|,
name|VECTORS_DOCUMENTS_EXTENSION
block|,
name|VECTORS_FIELDS_EXTENSION
block|,
name|GEN_EXTENSION
block|,
name|NORMS_EXTENSION
block|,
name|COMPOUND_FILE_STORE_EXTENSION
block|,   }
decl_stmt|;
comment|/** File extensions that are added to a compound file    * (same as above, minus "del", "gen", "cfs"). */
DECL|field|INDEX_EXTENSIONS_IN_COMPOUND_FILE
specifier|static
specifier|final
name|String
index|[]
name|INDEX_EXTENSIONS_IN_COMPOUND_FILE
init|=
operator|new
name|String
index|[]
block|{
name|FIELD_INFOS_EXTENSION
block|,
name|FIELDS_INDEX_EXTENSION
block|,
name|FIELDS_EXTENSION
block|,
name|TERMS_INDEX_EXTENSION
block|,
name|TERMS_EXTENSION
block|,
name|FREQ_EXTENSION
block|,
name|PROX_EXTENSION
block|,
name|VECTORS_INDEX_EXTENSION
block|,
name|VECTORS_DOCUMENTS_EXTENSION
block|,
name|VECTORS_FIELDS_EXTENSION
block|,
name|NORMS_EXTENSION
block|}
decl_stmt|;
DECL|field|STORE_INDEX_EXTENSIONS
specifier|static
specifier|final
name|String
index|[]
name|STORE_INDEX_EXTENSIONS
init|=
operator|new
name|String
index|[]
block|{
name|VECTORS_INDEX_EXTENSION
block|,
name|VECTORS_FIELDS_EXTENSION
block|,
name|VECTORS_DOCUMENTS_EXTENSION
block|,
name|FIELDS_INDEX_EXTENSION
block|,
name|FIELDS_EXTENSION
block|}
decl_stmt|;
DECL|field|NON_STORE_INDEX_EXTENSIONS
specifier|static
specifier|final
name|String
index|[]
name|NON_STORE_INDEX_EXTENSIONS
init|=
operator|new
name|String
index|[]
block|{
name|FIELD_INFOS_EXTENSION
block|,
name|FREQ_EXTENSION
block|,
name|PROX_EXTENSION
block|,
name|TERMS_EXTENSION
block|,
name|TERMS_INDEX_EXTENSION
block|,
name|NORMS_EXTENSION
block|}
decl_stmt|;
comment|/** File extensions of old-style index files */
DECL|field|COMPOUND_EXTENSIONS
specifier|static
specifier|final
name|String
name|COMPOUND_EXTENSIONS
index|[]
init|=
operator|new
name|String
index|[]
block|{
name|FIELD_INFOS_EXTENSION
block|,
name|FREQ_EXTENSION
block|,
name|PROX_EXTENSION
block|,
name|FIELDS_INDEX_EXTENSION
block|,
name|FIELDS_EXTENSION
block|,
name|TERMS_INDEX_EXTENSION
block|,
name|TERMS_EXTENSION
block|}
decl_stmt|;
comment|/** File extensions for term vector support */
DECL|field|VECTOR_EXTENSIONS
specifier|static
specifier|final
name|String
name|VECTOR_EXTENSIONS
index|[]
init|=
operator|new
name|String
index|[]
block|{
name|VECTORS_INDEX_EXTENSION
block|,
name|VECTORS_DOCUMENTS_EXTENSION
block|,
name|VECTORS_FIELDS_EXTENSION
block|}
decl_stmt|;
comment|/**    * Computes the full file name from base, extension and    * generation.  If the generation is -1, the file name is    * null.  If it's 0, the file name is<base><extension>.    * If it's> 0, the file name is<base>_<generation><extension>.    *    * @param base -- main part of the file name    * @param extension -- extension of the filename (including .)    * @param gen -- generation    */
DECL|method|fileNameFromGeneration
specifier|static
specifier|final
name|String
name|fileNameFromGeneration
parameter_list|(
name|String
name|base
parameter_list|,
name|String
name|extension
parameter_list|,
name|long
name|gen
parameter_list|)
block|{
if|if
condition|(
name|gen
operator|==
name|SegmentInfo
operator|.
name|NO
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|gen
operator|==
name|SegmentInfo
operator|.
name|WITHOUT_GEN
condition|)
block|{
return|return
name|base
operator|+
name|extension
return|;
block|}
else|else
block|{
return|return
name|base
operator|+
literal|"_"
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|gen
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
operator|+
name|extension
return|;
block|}
block|}
block|}
end_class
end_unit
