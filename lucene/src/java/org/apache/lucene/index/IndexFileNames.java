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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|Codec
import|;
end_import
begin_comment
comment|// TODO: put all files under codec and remove all the static extensions here
end_comment
begin_comment
comment|/**  * This class contains useful constants representing filenames and extensions  * used by lucene, as well as convenience methods for querying whether a file  * name matches an extension ({@link #matchesExtension(String, String)  * matchesExtension}), as well as generating file names from a segment name,  * generation and extension (  * {@link #fileNameFromGeneration(String, String, long) fileNameFromGeneration},  * {@link #segmentFileName(String, String, String) segmentFileName}).  *  *<p><b>NOTE</b>: extensions used by codecs are not  * listed here.  You must interact with the {@link Codec}  * directly.  *  * @lucene.internal  */
end_comment
begin_class
DECL|class|IndexFileNames
specifier|public
specifier|final
class|class
name|IndexFileNames
block|{
comment|/** Name of the index segment file */
DECL|field|SEGMENTS
specifier|public
specifier|static
specifier|final
name|String
name|SEGMENTS
init|=
literal|"segments"
decl_stmt|;
comment|/** Extension of gen file */
DECL|field|GEN_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|GEN_EXTENSION
init|=
literal|"gen"
decl_stmt|;
comment|/** Name of the generation reference file name */
DECL|field|SEGMENTS_GEN
specifier|public
specifier|static
specifier|final
name|String
name|SEGMENTS_GEN
init|=
literal|"segments."
operator|+
name|GEN_EXTENSION
decl_stmt|;
comment|/** Extension of compound file */
DECL|field|COMPOUND_FILE_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|COMPOUND_FILE_EXTENSION
init|=
literal|"cfs"
decl_stmt|;
comment|/** Extension of compound file entries */
DECL|field|COMPOUND_FILE_ENTRIES_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|COMPOUND_FILE_ENTRIES_EXTENSION
init|=
literal|"cfe"
decl_stmt|;
comment|/** Extension of compound file for doc store files*/
DECL|field|COMPOUND_FILE_STORE_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|COMPOUND_FILE_STORE_EXTENSION
init|=
literal|"cfx"
decl_stmt|;
comment|/**    * This array contains all filename extensions used by    * Lucene's index files, with one exception, namely the    * extension made up from<code>.s</code> + a number.    * Also note that Lucene's<code>segments_N</code> files    * do not have any filename extension.    */
DECL|field|INDEX_EXTENSIONS
specifier|public
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
name|COMPOUND_FILE_ENTRIES_EXTENSION
block|,
name|GEN_EXTENSION
block|,
name|COMPOUND_FILE_STORE_EXTENSION
block|,   }
decl_stmt|;
comment|/**    * Computes the full file name from base, extension and generation. If the    * generation is -1, the file name is null. If it's 0, the file name is    *&lt;base&gt;.&lt;ext&gt;. If it's> 0, the file name is    *&lt;base&gt;_&lt;gen&gt;.&lt;ext&gt;.<br>    *<b>NOTE:</b> .&lt;ext&gt; is added to the name only if<code>ext</code> is    * not an empty string.    *     * @param base main part of the file name    * @param ext extension of the filename    * @param gen generation    */
DECL|method|fileNameFromGeneration
specifier|public
specifier|static
name|String
name|fileNameFromGeneration
parameter_list|(
name|String
name|base
parameter_list|,
name|String
name|ext
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
name|segmentFileName
argument_list|(
name|base
argument_list|,
literal|""
argument_list|,
name|ext
argument_list|)
return|;
block|}
else|else
block|{
comment|// The '6' part in the length is: 1 for '.', 1 for '_' and 4 as estimate
comment|// to the gen length as string (hopefully an upper limit so SB won't
comment|// expand in the middle.
name|StringBuilder
name|res
init|=
operator|new
name|StringBuilder
argument_list|(
name|base
operator|.
name|length
argument_list|()
operator|+
literal|6
operator|+
name|ext
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|base
argument_list|)
operator|.
name|append
argument_list|(
literal|'_'
argument_list|)
operator|.
name|append
argument_list|(
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
argument_list|)
decl_stmt|;
if|if
condition|(
name|ext
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|res
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
operator|.
name|append
argument_list|(
name|ext
argument_list|)
expr_stmt|;
block|}
return|return
name|res
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**    * Returns a file name that includes the given segment name, your own custom    * name and extension. The format of the filename is:    *&lt;segmentName&gt;(_&lt;name&gt;)(.&lt;ext&gt;).    *<p>    *<b>NOTE:</b> .&lt;ext&gt; is added to the result file name only if    *<code>ext</code> is not empty.    *<p>    *<b>NOTE:</b> _&lt;segmentSuffix&gt; is added to the result file name only if    * it's not the empty string    *<p>    *<b>NOTE:</b> all custom files should be named using this method, or    * otherwise some structures may fail to handle them properly (such as if they    * are added to compound files).    */
DECL|method|segmentFileName
specifier|public
specifier|static
name|String
name|segmentFileName
parameter_list|(
name|String
name|segmentName
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|String
name|ext
parameter_list|)
block|{
if|if
condition|(
name|ext
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|||
name|segmentSuffix
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
assert|assert
operator|!
name|ext
operator|.
name|startsWith
argument_list|(
literal|"."
argument_list|)
assert|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|segmentName
operator|.
name|length
argument_list|()
operator|+
literal|2
operator|+
name|segmentSuffix
operator|.
name|length
argument_list|()
operator|+
name|ext
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|segmentName
argument_list|)
expr_stmt|;
if|if
condition|(
name|segmentSuffix
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'_'
argument_list|)
operator|.
name|append
argument_list|(
name|segmentSuffix
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ext
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
operator|.
name|append
argument_list|(
name|ext
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|segmentName
return|;
block|}
block|}
comment|/**    * Returns true if the given filename ends with the given extension. One    * should provide a<i>pure</i> extension, without '.'.    */
DECL|method|matchesExtension
specifier|public
specifier|static
name|boolean
name|matchesExtension
parameter_list|(
name|String
name|filename
parameter_list|,
name|String
name|ext
parameter_list|)
block|{
comment|// It doesn't make a difference whether we allocate a StringBuilder ourself
comment|// or not, since there's only 1 '+' operator.
return|return
name|filename
operator|.
name|endsWith
argument_list|(
literal|"."
operator|+
name|ext
argument_list|)
return|;
block|}
comment|/**    * Strips the segment name out of the given file name. If you used    * {@link #segmentFileName} or {@link #fileNameFromGeneration} to create your    * files, then this method simply removes whatever comes before the first '.',    * or the second '_' (excluding both).    *     * @return the filename with the segment name removed, or the given filename    *         if it does not contain a '.' and '_'.    */
DECL|method|stripSegmentName
specifier|public
specifier|static
name|String
name|stripSegmentName
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
comment|// If it is a .del file, there's an '_' after the first character
name|int
name|idx
init|=
name|filename
operator|.
name|indexOf
argument_list|(
literal|'_'
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|==
operator|-
literal|1
condition|)
block|{
comment|// If it's not, strip everything that's before the '.'
name|idx
operator|=
name|filename
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|idx
operator|!=
operator|-
literal|1
condition|)
block|{
name|filename
operator|=
name|filename
operator|.
name|substring
argument_list|(
name|idx
argument_list|)
expr_stmt|;
block|}
return|return
name|filename
return|;
block|}
DECL|method|stripExtension
specifier|public
specifier|static
name|String
name|stripExtension
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
name|int
name|idx
init|=
name|filename
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|!=
operator|-
literal|1
condition|)
block|{
name|filename
operator|=
name|filename
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
return|return
name|filename
return|;
block|}
block|}
end_class
end_unit
