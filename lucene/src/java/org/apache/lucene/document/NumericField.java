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
name|index
operator|.
name|FieldInfo
operator|.
name|IndexOptions
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
name|NumericUtils
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
name|FieldCache
import|;
end_import
begin_comment
comment|// javadocs
end_comment
begin_comment
comment|/**  *<p>This class provides a {@link Field} that enables indexing  * of numeric values for efficient range filtering and  * sorting.  Here's an example usage, adding an int value:  *<pre>  *  document.add(new NumericField(name).setIntValue(value));  *</pre>  *  * For optimal performance, re-use the  *<code>NumericField</code> and {@link Document} instance for more than  * one document:  *  *<pre>  *  NumericField field = new NumericField(name);  *  Document document = new Document();  *  document.add(field);  *  *  for(all documents) {  *    ...  *    field.setIntValue(value)  *    writer.addDocument(document);  *    ...  *  }  *</pre>  *  *<p>The java native types<code>int</code>,<code>long</code>,  *<code>float</code> and<code>double</code> are  * directly supported.  However, any value that can be  * converted into these native types can also be indexed.  * For example, date/time values represented by a  * {@link java.util.Date} can be translated into a long  * value using the {@link java.util.Date#getTime} method.  If you  * don't need millisecond precision, you can quantize the  * value, either by dividing the result of  * {@link java.util.Date#getTime} or using the separate getters  * (for year, month, etc.) to construct an<code>int</code> or  *<code>long</code> value.</p>  *  *<p>To perform range querying or filtering against a  *<code>NumericField</code>, use {@link NumericRangeQuery} or {@link  * NumericRangeFilter}.  To sort according to a  *<code>NumericField</code>, use the normal numeric sort types, eg  * {@link org.apache.lucene.search.SortField.Type#INT}.<code>NumericField</code>   * values can also be loaded directly from {@link FieldCache}.</p>  *  *<p>By default, a<code>NumericField</code>'s value is not stored but  * is indexed for range filtering and sorting.  You can use  * the {@link #NumericField(String,Field.Store,boolean)}  * constructor if you need to change these defaults.</p>  *  *<p>You may add the same field name as a<code>NumericField</code> to  * the same document more than once.  Range querying and  * filtering will be the logical OR of all values; so a range query  * will hit all documents that have at least one value in  * the range. However sort behavior is not defined.  If you need to sort,  * you should separately index a single-valued<code>NumericField</code>.</p>  *  *<p>A<code>NumericField</code> will consume somewhat more disk space  * in the index than an ordinary single-valued field.  * However, for a typical index that includes substantial  * textual content per document, this increase will likely  * be in the noise.</p>  *  *<p>Within Lucene, each numeric value is indexed as a  *<em>trie</em> structure, where each term is logically  * assigned to larger and larger pre-defined brackets (which  * are simply lower-precision representations of the value).  * The step size between each successive bracket is called the  *<code>precisionStep</code>, measured in bits.  Smaller  *<code>precisionStep</code> values result in larger number  * of brackets, which consumes more disk space in the index  * but may result in faster range search performance.  The  * default value, 4, was selected for a reasonable tradeoff  * of disk space consumption versus performance.  You can  * use the expert constructor {@link  * #NumericField(String,int,Field.Store,boolean)} if you'd  * like to change the value.  Note that you must also  * specify a congruent value when creating {@link  * NumericRangeQuery} or {@link NumericRangeFilter}.  * For low cardinality fields larger precision steps are good.  * If the cardinality is&lt; 100, it is fair  * to use {@link Integer#MAX_VALUE}, which produces one  * term per value.  *  *<p>For more information on the internals of numeric trie  * indexing, including the<a  * href="../search/NumericRangeQuery.html#precisionStepDesc"><code>precisionStep</code></a>  * configuration, see {@link NumericRangeQuery}. The format of  * indexed values is described in {@link NumericUtils}.  *  *<p>If you only need to sort by numeric value, and never  * run range querying/filtering, you can index using a  *<code>precisionStep</code> of {@link Integer#MAX_VALUE}.  * This will minimize disk space consumed.</p>  *  *<p>More advanced users can instead use {@link  * NumericTokenStream} directly, when indexing numbers. This  * class is a wrapper around this token stream type for  * easier, more intuitive usage.</p>  *  * @since 2.9  */
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
comment|/** Data type of the value in {@link NumericField}.    * @since 3.2    */
DECL|enum|DataType
DECL|enum constant|INT
DECL|enum constant|LONG
DECL|enum constant|FLOAT
DECL|enum constant|DOUBLE
specifier|public
specifier|static
enum|enum
name|DataType
block|{
name|INT
block|,
name|LONG
block|,
name|FLOAT
block|,
name|DOUBLE
block|}
DECL|field|numericTS
specifier|private
specifier|transient
name|NumericTokenStream
name|numericTS
decl_stmt|;
DECL|field|type
specifier|private
name|DataType
name|type
decl_stmt|;
DECL|field|precisionStep
specifier|private
specifier|final
name|int
name|precisionStep
decl_stmt|;
comment|/**    * Creates a field for numeric values using the default<code>precisionStep</code>    * {@link NumericUtils#PRECISION_STEP_DEFAULT} (4). The instance is not yet initialized with    * a numeric value, before indexing a document containing this field,    * set a value using the various set<em>???</em>Value() methods.    * This constructor creates an indexed, but not stored field.    * @param name the field name    */
DECL|method|NumericField
specifier|public
name|NumericField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|NumericUtils
operator|.
name|PRECISION_STEP_DEFAULT
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
comment|/**    * Creates a field for numeric values using the default<code>precisionStep</code>    * {@link NumericUtils#PRECISION_STEP_DEFAULT} (4). The instance is not yet initialized with    * a numeric value, before indexing a document containing this field,    * set a value using the various set<em>???</em>Value() methods.    * @param name the field name    * @param store if the field should be stored, {@link Document#getFieldable}    * then returns {@code NumericField} instances on search results.    * @param index if the field should be indexed using {@link NumericTokenStream}    */
DECL|method|NumericField
specifier|public
name|NumericField
parameter_list|(
name|String
name|name
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
name|this
argument_list|(
name|name
argument_list|,
name|NumericUtils
operator|.
name|PRECISION_STEP_DEFAULT
argument_list|,
name|store
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a field for numeric values with the specified    *<code>precisionStep</code>. The instance is not yet initialized with    * a numeric value, before indexing a document containing this field,    * set a value using the various set<em>???</em>Value() methods.    * This constructor creates an indexed, but not stored field.    * @param name the field name    * @param precisionStep the used<a href="../search/NumericRangeQuery.html#precisionStepDesc">precision step</a>    */
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
comment|/**    * Creates a field for numeric values with the specified    *<code>precisionStep</code>. The instance is not yet initialized with    * a numeric value, before indexing a document containing this field,    * set a value using the various set<em>???</em>Value() methods.    * @param name the field name    * @param precisionStep the used<a href="../search/NumericRangeQuery.html#precisionStepDesc">precision step</a>    * @param store if the field should be stored, {@link Document#getFieldable}    * then returns {@code NumericField} instances on search results.    * @param index if the field should be indexed using {@link NumericTokenStream}    */
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
name|this
operator|.
name|precisionStep
operator|=
name|precisionStep
expr_stmt|;
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_ONLY
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
if|if
condition|(
operator|!
name|isIndexed
argument_list|()
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|numericTS
operator|==
literal|null
condition|)
block|{
comment|// lazy init the TokenStream as it is heavy to instantiate (attributes,...),
comment|// if not needed (stored field loading)
name|numericTS
operator|=
operator|new
name|NumericTokenStream
argument_list|(
name|precisionStep
argument_list|)
expr_stmt|;
comment|// initialize value in TokenStream
if|if
condition|(
name|fieldsData
operator|!=
literal|null
condition|)
block|{
assert|assert
name|type
operator|!=
literal|null
assert|;
specifier|final
name|Number
name|val
init|=
operator|(
name|Number
operator|)
name|fieldsData
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|INT
case|:
name|numericTS
operator|.
name|setIntValue
argument_list|(
name|val
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|LONG
case|:
name|numericTS
operator|.
name|setLongValue
argument_list|(
name|val
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
name|numericTS
operator|.
name|setFloatValue
argument_list|(
name|val
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|numericTS
operator|.
name|setDoubleValue
argument_list|(
name|val
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
assert|assert
literal|false
operator|:
literal|"Should never get here"
assert|;
block|}
block|}
block|}
return|return
name|numericTS
return|;
block|}
comment|/** Returns always<code>null</code> for numeric fields */
annotation|@
name|Override
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
comment|/** Returns the numeric value as a string. This format is also returned if you call {@link Document#get(String)}    * on search results. It is recommended to use {@link Document#getFieldable} instead    * that returns {@code NumericField} instances. You can then use {@link #getNumericValue}    * to return the stored value. */
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
comment|/** Returns the precision step. */
DECL|method|getPrecisionStep
specifier|public
name|int
name|getPrecisionStep
parameter_list|()
block|{
return|return
name|precisionStep
return|;
block|}
comment|/** Returns the data type of the current value, {@code null} if not yet set.    * @since 3.2    */
DECL|method|getDataType
specifier|public
name|DataType
name|getDataType
parameter_list|()
block|{
return|return
name|type
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
if|if
condition|(
name|numericTS
operator|!=
literal|null
condition|)
name|numericTS
operator|.
name|setLongValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|type
operator|=
name|DataType
operator|.
name|LONG
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
if|if
condition|(
name|numericTS
operator|!=
literal|null
condition|)
name|numericTS
operator|.
name|setIntValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|type
operator|=
name|DataType
operator|.
name|INT
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
if|if
condition|(
name|numericTS
operator|!=
literal|null
condition|)
name|numericTS
operator|.
name|setDoubleValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
name|Double
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|type
operator|=
name|DataType
operator|.
name|DOUBLE
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
if|if
condition|(
name|numericTS
operator|!=
literal|null
condition|)
name|numericTS
operator|.
name|setFloatValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
name|Float
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|type
operator|=
name|DataType
operator|.
name|FLOAT
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class
end_unit
