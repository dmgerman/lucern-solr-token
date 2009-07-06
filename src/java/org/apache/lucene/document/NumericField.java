begin_unit
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|NumericTokenStream
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
name|search
operator|.
name|NumericRangeQuery
import|;
end_import
begin_comment
comment|// javadocs
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|NumericRangeFilter
import|;
end_import
begin_comment
comment|// javadocs
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|SortField
import|;
end_import
begin_comment
comment|// javadocs
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|FieldCache
import|;
end_import
begin_comment
comment|// javadocs
end_comment
begin_comment
comment|/**  * This class provides a {@link Field} for indexing numeric values  * that can be used by {@link NumericRangeQuery}/{@link NumericRangeFilter}.  * For more information, how to use this class and its configuration properties  * (<a href="../search/NumericRangeQuery.html#precisionStepDesc"><code>precisionStep</code></a>)  * read the docs of {@link NumericRangeQuery}.  *  *<p>A numeric value is indexed as multiple string encoded terms, each reduced  * by zeroing bits from the right. Each value is also prefixed (in the first char) by the  *<code>shift</code> value (number of bits removed) used during encoding.  * The number of bits removed from the right for each trie entry is called  *<code>precisionStep</code> in this API.  *  *<p>The usage pattern is:  *<pre>  *  document.add(  *   new NumericField(name, precisionStep, Field.Store.XXX, true).set<em>???</em>Value(value)  *  );  *</pre>  *<p>For optimal performance, re-use the NumericField and {@link Document} instance  * for more than one document:  *<pre>  *<em>// init</em>  *  NumericField field = new NumericField(name, precisionStep, Field.Store.XXX, true);  *  Document document = new Document();  *  document.add(field);  *<em>// use this code to index many documents:</em>  *  field.set<em>???</em>Value(value1)  *  writer.addDocument(document);  *  field.set<em>???</em>Value(value2)  *  writer.addDocument(document);  *  ...  *</pre>  *  *<p>More advanced users can instead use {@link NumericTokenStream} directly, when  * indexing numbers. This class is a wrapper around this token stream type for easier,  * more intuitive usage.  *  *<p><b>Please note:</b> This class is only used during indexing. You can also create  * numeric stored fields with it, but when retrieving the stored field value  * from a {@link Document} instance after search, you will get a conventional  * {@link Fieldable} instance where the numeric values are returned as {@link String}s  * (according to<code>toString(value)</code> of the used data type).  *  *<p>Values indexed by this field can be loaded into the {@link FieldCache}  * and can be sorted (use {@link SortField}{@code .TYPE} to specify the correct  * type; {@link SortField#AUTO} does not work with this type of field)  *  *<p><font color="red"><b>NOTE:</b> This API is experimental and  * might change in incompatible ways in the next release.</font>  *  * @since 2.9  */
end_comment
begin_class
DECL|class|NumericField
specifier|public
specifier|final
class|class
name|NumericField
extends|extends
name|AbstractField
block|{
DECL|field|tokenStream
specifier|private
specifier|final
name|NumericTokenStream
name|tokenStream
decl_stmt|;
comment|/**    * Creates a field for numeric values. The instance is not yet initialized with    * a numeric value, before indexing a document containing this field,    * set a value using the various set<em>???</em>Value() methods.    * This constrcutor creates an indexed, but not stored field.    * @param name the field name    * @param precisionStep the used<a href="../search/NumericRangeQuery.html#precisionStepDesc">precision step</a>    */
DECL|method|NumericField
specifier|public
name|NumericField
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|precisionStep
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|precisionStep
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a field for numeric values. The instance is not yet initialized with    * a numeric value, before indexing a document containing this field,    * set a value using the various set<em>???</em>Value() methods.    * @param name the field name    * @param precisionStep the used<a href="../search/NumericRangeQuery.html#precisionStepDesc">precision step</a>    * @param store if the field should be stored in plain text form    *  (according to<code>toString(value)</code> of the used data type)    * @param index if the field should be indexed using {@link NumericTokenStream}    */
DECL|method|NumericField
specifier|public
name|NumericField
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|precisionStep
parameter_list|,
name|Field
operator|.
name|Store
name|store
parameter_list|,
name|boolean
name|index
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|index
condition|?
name|Field
operator|.
name|Index
operator|.
name|ANALYZED_NO_NORMS
else|:
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
expr_stmt|;
name|setOmitTermFreqAndPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|tokenStream
operator|=
operator|new
name|NumericTokenStream
argument_list|(
name|precisionStep
argument_list|)
expr_stmt|;
block|}
comment|/** Returns a {@link NumericTokenStream} for indexing the numeric value. */
DECL|method|tokenStreamValue
specifier|public
name|TokenStream
name|tokenStreamValue
parameter_list|()
block|{
return|return
name|isIndexed
argument_list|()
condition|?
name|tokenStream
else|:
literal|null
return|;
block|}
comment|/** Returns always<code>null</code> for numeric fields */
DECL|method|binaryValue
specifier|public
name|byte
index|[]
name|binaryValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** Returns always<code>null</code> for numeric fields */
DECL|method|getBinaryValue
specifier|public
name|byte
index|[]
name|getBinaryValue
parameter_list|(
name|byte
index|[]
name|result
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|/** Returns always<code>null</code> for numeric fields */
DECL|method|readerValue
specifier|public
name|Reader
name|readerValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** Returns the numeric value as a string (how it is stored, when {@link Field.Store#YES} is choosen). */
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|()
block|{
return|return
operator|(
name|fieldsData
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|fieldsData
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Returns the current numeric value as a subclass of {@link Number},<code>null</code> if not yet initialized. */
DECL|method|getNumericValue
specifier|public
name|Number
name|getNumericValue
parameter_list|()
block|{
return|return
operator|(
name|Number
operator|)
name|fieldsData
return|;
block|}
comment|/**    * Initializes the field with the supplied<code>long</code> value.    * @param value the numeric value    * @return this instance, because of this you can use it the following way:    *<code>document.add(new NumericField(name, precisionStep).setLongValue(value))</code>    */
DECL|method|setLongValue
specifier|public
name|NumericField
name|setLongValue
parameter_list|(
specifier|final
name|long
name|value
parameter_list|)
block|{
name|tokenStream
operator|.
name|setLongValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
operator|new
name|Long
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Initializes the field with the supplied<code>int</code> value.    * @param value the numeric value    * @return this instance, because of this you can use it the following way:    *<code>document.add(new NumericField(name, precisionStep).setIntValue(value))</code>    */
DECL|method|setIntValue
specifier|public
name|NumericField
name|setIntValue
parameter_list|(
specifier|final
name|int
name|value
parameter_list|)
block|{
name|tokenStream
operator|.
name|setIntValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
operator|new
name|Integer
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Initializes the field with the supplied<code>double</code> value.    * @param value the numeric value    * @return this instance, because of this you can use it the following way:    *<code>document.add(new NumericField(name, precisionStep).setDoubleValue(value))</code>    */
DECL|method|setDoubleValue
specifier|public
name|NumericField
name|setDoubleValue
parameter_list|(
specifier|final
name|double
name|value
parameter_list|)
block|{
name|tokenStream
operator|.
name|setDoubleValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
operator|new
name|Double
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Initializes the field with the supplied<code>float</code> value.    * @param value the numeric value    * @return this instance, because of this you can use it the following way:    *<code>document.add(new NumericField(name, precisionStep).setFloatValue(value))</code>    */
DECL|method|setFloatValue
specifier|public
name|NumericField
name|setFloatValue
parameter_list|(
specifier|final
name|float
name|value
parameter_list|)
block|{
name|tokenStream
operator|.
name|setFloatValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
operator|new
name|Float
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class
end_unit
