begin_unit
begin_package
DECL|package|org.apache.lucene.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|function
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A query that scores each document as the value of the numeric input field.  *<p>   * The query matches all documents, and scores each document according to the numeric   * value of that field.   *<p>  * It is assumed, and expected, that:  *<ul>  *<li>The field used here is indexed, and has exactly   *      one token in every scored document.</li>   *<li>Best if this field is un_tokenized.</li>  *<li>That token is parseable to the selected type.</li>  *</ul>  *<p>    * Combining this query in a FunctionQuery allows much freedom in affecting document scores.  * Note, that with this freedom comes responsibility: it is more than likely that the  * default Lucene scoring is superior in quality to scoring modified as explained here.  * However, in some cases, and certainly for research experiments, this capability may turn useful.  *<p>  * When constructing this query, select the appropriate type. That type should match the data stored in the  * field. So in fact the "right" type should be selected before indexing. Type selection  * has effect on the RAM usage:   *<ul>  *<li>{@link Type#BYTE} consumes 1 * maxDocs bytes.</li>  *<li>{@link Type#SHORT} consumes 2 * maxDocs bytes.</li>  *<li>{@link Type#INT} consumes 4 * maxDocs bytes.</li>  *<li>{@link Type#FLOAT} consumes 8 * maxDocs bytes.</li>  *</ul>  *<p>  *<b>Caching:</b>  * Values for the numeric field are loaded once and cached in memory for further use with the same IndexReader.   * To take advantage of this, it is extremely important to reuse index-readers or index-searchers,   * otherwise, for instance if for each query a new index reader is opened, large penalties would be   * paid for loading the field values into memory over and over again!  *   *<p><font color="#FF0000">  * WARNING: The status of the<b>search.function</b> package is experimental.   * The APIs introduced here might change in the future and will not be   * supported anymore in such a case.</font>  */
end_comment
begin_class
DECL|class|FieldScoreQuery
specifier|public
class|class
name|FieldScoreQuery
extends|extends
name|ValueSourceQuery
block|{
comment|/**    * Type of score field, indicating how field values are interpreted/parsed.      *<p>    * The type selected at search search time should match the data stored in the field.     * Different types have different RAM requirements:     *<ul>    *<li>{@link #BYTE} consumes 1 * maxDocs bytes.</li>    *<li>{@link #SHORT} consumes 2 * maxDocs bytes.</li>    *<li>{@link #INT} consumes 4 * maxDocs bytes.</li>    *<li>{@link #FLOAT} consumes 8 * maxDocs bytes.</li>    *</ul>    */
DECL|class|Type
specifier|public
specifier|static
class|class
name|Type
block|{
comment|/** field values are interpreted as numeric byte values. */
DECL|field|BYTE
specifier|public
specifier|static
specifier|final
name|Type
name|BYTE
init|=
operator|new
name|Type
argument_list|(
literal|"byte"
argument_list|)
decl_stmt|;
comment|/** field values are interpreted as numeric short values. */
DECL|field|SHORT
specifier|public
specifier|static
specifier|final
name|Type
name|SHORT
init|=
operator|new
name|Type
argument_list|(
literal|"short"
argument_list|)
decl_stmt|;
comment|/** field values are interpreted as numeric int values. */
DECL|field|INT
specifier|public
specifier|static
specifier|final
name|Type
name|INT
init|=
operator|new
name|Type
argument_list|(
literal|"int"
argument_list|)
decl_stmt|;
comment|/** field values are interpreted as numeric float values. */
DECL|field|FLOAT
specifier|public
specifier|static
specifier|final
name|Type
name|FLOAT
init|=
operator|new
name|Type
argument_list|(
literal|"float"
argument_list|)
decl_stmt|;
DECL|field|typeName
specifier|private
name|String
name|typeName
decl_stmt|;
DECL|method|Type
specifier|private
name|Type
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|typeName
operator|=
name|name
expr_stmt|;
block|}
comment|/*(non-Javadoc) @see java.lang.Object#toString() */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"::"
operator|+
name|typeName
return|;
block|}
block|}
comment|/**    * Create a FieldScoreQuery - a query that scores each document as the value of the numeric input field.    *<p>    * The<code>type</code> param tells how to parse the field string values into a numeric score value.    * @param field the numeric field to be used.    * @param type the type of the field: either    * {@link Type#BYTE}, {@link Type#SHORT}, {@link Type#INT}, or {@link Type#FLOAT}.     */
DECL|method|FieldScoreQuery
specifier|public
name|FieldScoreQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|getValueSource
argument_list|(
name|field
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// create the appropriate (cached) field value source.
DECL|method|getValueSource
specifier|private
specifier|static
name|ValueSource
name|getValueSource
parameter_list|(
name|String
name|field
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|BYTE
condition|)
block|{
return|return
operator|new
name|ByteFieldSource
argument_list|(
name|field
argument_list|)
return|;
block|}
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|SHORT
condition|)
block|{
return|return
operator|new
name|ShortFieldSource
argument_list|(
name|field
argument_list|)
return|;
block|}
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|INT
condition|)
block|{
return|return
operator|new
name|IntFieldSource
argument_list|(
name|field
argument_list|)
return|;
block|}
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|FLOAT
condition|)
block|{
return|return
operator|new
name|FloatFieldSource
argument_list|(
name|field
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|type
operator|+
literal|" is not a known Field Score Query Type!"
argument_list|)
throw|;
block|}
block|}
end_class
end_unit
