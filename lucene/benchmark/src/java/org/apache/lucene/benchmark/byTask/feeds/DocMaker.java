begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import
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
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParsePosition
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|FieldType
operator|.
name|NumericType
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
name|document
operator|.
name|FieldType
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
name|document
operator|.
name|IntField
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
name|document
operator|.
name|LongField
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
name|document
operator|.
name|FloatField
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
name|document
operator|.
name|DoubleField
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
name|document
operator|.
name|StringField
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
name|document
operator|.
name|TextField
import|;
end_import
begin_comment
comment|/**  * Creates {@link Document} objects. Uses a {@link ContentSource} to generate  * {@link DocData} objects. Supports the following parameters:  *<ul>  *<li><b>content.source</b> - specifies the {@link ContentSource} class to use  * (default<b>SingleDocSource</b>).  *<li><b>doc.stored</b> - specifies whether fields should be stored (default  *<b>false</b>).  *<li><b>doc.body.stored</b> - specifies whether the body field should be stored (default  * =<b>doc.stored</b>).  *<li><b>doc.tokenized</b> - specifies whether fields should be tokenized  * (default<b>true</b>).  *<li><b>doc.body.tokenized</b> - specifies whether the  * body field should be tokenized (default =<b>doc.tokenized</b>).  *<li><b>doc.tokenized.norms</b> - specifies whether norms should be stored in  * the index or not. (default<b>false</b>).  *<li><b>doc.body.tokenized.norms</b> - specifies whether norms should be  * stored in the index for the body field. This can be set to true, while  *<code>doc.tokenized.norms</code> is set to false, to allow norms storing just  * for the body field. (default<b>true</b>).  *<li><b>doc.term.vector</b> - specifies whether term vectors should be stored  * for fields (default<b>false</b>).  *<li><b>doc.term.vector.positions</b> - specifies whether term vectors should  * be stored with positions (default<b>false</b>).  *<li><b>doc.term.vector.offsets</b> - specifies whether term vectors should be  * stored with offsets (default<b>false</b>).  *<li><b>doc.store.body.bytes</b> - specifies whether to store the raw bytes of  * the document's content in the document (default<b>false</b>).  *<li><b>doc.reuse.fields</b> - specifies whether Field and Document objects  * should be reused (default<b>true</b>).  *<li><b>doc.index.props</b> - specifies whether the properties returned by  *<li><b>doc.random.id.limit</b> - if specified, docs will be assigned random  * IDs from 0 to this limit.  This is useful with UpdateDoc  * for testing performance of IndexWriter.updateDocument.  * {@link DocData#getProps()} will be indexed. (default<b>false</b>).  *</ul>  */
end_comment
begin_class
DECL|class|DocMaker
specifier|public
class|class
name|DocMaker
implements|implements
name|Closeable
block|{
DECL|class|LeftOver
specifier|private
specifier|static
class|class
name|LeftOver
block|{
DECL|field|docdata
specifier|private
name|DocData
name|docdata
decl_stmt|;
DECL|field|cnt
specifier|private
name|int
name|cnt
decl_stmt|;
block|}
DECL|field|r
specifier|private
name|Random
name|r
decl_stmt|;
DECL|field|updateDocIDLimit
specifier|private
name|int
name|updateDocIDLimit
decl_stmt|;
comment|/**    * Document state, supports reuse of field instances    * across documents (see<code>reuseFields</code> parameter).    */
DECL|class|DocState
specifier|protected
specifier|static
class|class
name|DocState
block|{
DECL|field|fields
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Field
argument_list|>
name|fields
decl_stmt|;
DECL|field|numericFields
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Field
argument_list|>
name|numericFields
decl_stmt|;
DECL|field|reuseFields
specifier|private
specifier|final
name|boolean
name|reuseFields
decl_stmt|;
DECL|field|doc
specifier|final
name|Document
name|doc
decl_stmt|;
DECL|field|docData
name|DocData
name|docData
init|=
operator|new
name|DocData
argument_list|()
decl_stmt|;
DECL|method|DocState
specifier|public
name|DocState
parameter_list|(
name|boolean
name|reuseFields
parameter_list|,
name|FieldType
name|ft
parameter_list|,
name|FieldType
name|bodyFt
parameter_list|)
block|{
name|this
operator|.
name|reuseFields
operator|=
name|reuseFields
expr_stmt|;
if|if
condition|(
name|reuseFields
condition|)
block|{
name|fields
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|numericFields
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
comment|// Initialize the map with the default fields.
name|fields
operator|.
name|put
argument_list|(
name|BODY_FIELD
argument_list|,
operator|new
name|Field
argument_list|(
name|BODY_FIELD
argument_list|,
literal|""
argument_list|,
name|bodyFt
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|TITLE_FIELD
argument_list|,
operator|new
name|Field
argument_list|(
name|TITLE_FIELD
argument_list|,
literal|""
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|DATE_FIELD
argument_list|,
operator|new
name|Field
argument_list|(
name|DATE_FIELD
argument_list|,
literal|""
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|ID_FIELD
argument_list|,
operator|new
name|StringField
argument_list|(
name|ID_FIELD
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|NAME_FIELD
argument_list|,
operator|new
name|Field
argument_list|(
name|NAME_FIELD
argument_list|,
literal|""
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|numericFields
operator|.
name|put
argument_list|(
name|DATE_MSEC_FIELD
argument_list|,
operator|new
name|LongField
argument_list|(
name|DATE_MSEC_FIELD
argument_list|,
literal|0L
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|numericFields
operator|.
name|put
argument_list|(
name|TIME_SEC_FIELD
argument_list|,
operator|new
name|IntField
argument_list|(
name|TIME_SEC_FIELD
argument_list|,
literal|0
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|numericFields
operator|=
literal|null
expr_stmt|;
name|fields
operator|=
literal|null
expr_stmt|;
name|doc
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**      * Returns a field corresponding to the field name. If      *<code>reuseFields</code> was set to true, then it attempts to reuse a      * Field instance. If such a field does not exist, it creates a new one.      */
DECL|method|getField
name|Field
name|getField
parameter_list|(
name|String
name|name
parameter_list|,
name|FieldType
name|ft
parameter_list|)
block|{
if|if
condition|(
operator|!
name|reuseFields
condition|)
block|{
return|return
operator|new
name|Field
argument_list|(
name|name
argument_list|,
literal|""
argument_list|,
name|ft
argument_list|)
return|;
block|}
name|Field
name|f
init|=
name|fields
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
block|{
name|f
operator|=
operator|new
name|Field
argument_list|(
name|name
argument_list|,
literal|""
argument_list|,
name|ft
argument_list|)
expr_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
return|return
name|f
return|;
block|}
DECL|method|getNumericField
name|Field
name|getNumericField
parameter_list|(
name|String
name|name
parameter_list|,
name|NumericType
name|type
parameter_list|)
block|{
name|Field
name|f
decl_stmt|;
if|if
condition|(
name|reuseFields
condition|)
block|{
name|f
operator|=
name|numericFields
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|f
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|==
literal|null
condition|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|INT
case|:
name|f
operator|=
operator|new
name|IntField
argument_list|(
name|name
argument_list|,
literal|0
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
break|break;
case|case
name|LONG
case|:
name|f
operator|=
operator|new
name|LongField
argument_list|(
name|name
argument_list|,
literal|0L
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
name|f
operator|=
operator|new
name|FloatField
argument_list|(
name|name
argument_list|,
literal|0.0F
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|f
operator|=
operator|new
name|DoubleField
argument_list|(
name|name
argument_list|,
literal|0.0
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Cannot get here"
argument_list|)
throw|;
block|}
if|if
condition|(
name|reuseFields
condition|)
block|{
name|numericFields
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|f
return|;
block|}
block|}
DECL|field|storeBytes
specifier|private
name|boolean
name|storeBytes
init|=
literal|false
decl_stmt|;
DECL|class|DateUtil
specifier|private
specifier|static
class|class
name|DateUtil
block|{
DECL|field|parser
specifier|public
name|SimpleDateFormat
name|parser
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"dd-MMM-yyyy HH:mm:ss"
argument_list|,
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
DECL|field|cal
specifier|public
name|Calendar
name|cal
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
DECL|field|pos
specifier|public
name|ParsePosition
name|pos
init|=
operator|new
name|ParsePosition
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|DateUtil
specifier|public
name|DateUtil
parameter_list|()
block|{
name|parser
operator|.
name|setLenient
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|// leftovers are thread local, because it is unsafe to share residues between threads
DECL|field|leftovr
specifier|private
name|ThreadLocal
argument_list|<
name|LeftOver
argument_list|>
name|leftovr
init|=
operator|new
name|ThreadLocal
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|docState
specifier|private
name|ThreadLocal
argument_list|<
name|DocState
argument_list|>
name|docState
init|=
operator|new
name|ThreadLocal
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|dateParsers
specifier|private
name|ThreadLocal
argument_list|<
name|DateUtil
argument_list|>
name|dateParsers
init|=
operator|new
name|ThreadLocal
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|BODY_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|BODY_FIELD
init|=
literal|"body"
decl_stmt|;
DECL|field|TITLE_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|TITLE_FIELD
init|=
literal|"doctitle"
decl_stmt|;
DECL|field|DATE_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|DATE_FIELD
init|=
literal|"docdate"
decl_stmt|;
DECL|field|DATE_MSEC_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|DATE_MSEC_FIELD
init|=
literal|"docdatenum"
decl_stmt|;
DECL|field|TIME_SEC_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|TIME_SEC_FIELD
init|=
literal|"doctimesecnum"
decl_stmt|;
DECL|field|ID_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|ID_FIELD
init|=
literal|"docid"
decl_stmt|;
DECL|field|BYTES_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|BYTES_FIELD
init|=
literal|"bytes"
decl_stmt|;
DECL|field|NAME_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|NAME_FIELD
init|=
literal|"docname"
decl_stmt|;
DECL|field|config
specifier|protected
name|Config
name|config
decl_stmt|;
DECL|field|valType
specifier|protected
name|FieldType
name|valType
decl_stmt|;
DECL|field|bodyValType
specifier|protected
name|FieldType
name|bodyValType
decl_stmt|;
DECL|field|source
specifier|protected
name|ContentSource
name|source
decl_stmt|;
DECL|field|reuseFields
specifier|protected
name|boolean
name|reuseFields
decl_stmt|;
DECL|field|indexProperties
specifier|protected
name|boolean
name|indexProperties
decl_stmt|;
DECL|field|numDocsCreated
specifier|private
specifier|final
name|AtomicInteger
name|numDocsCreated
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|method|DocMaker
specifier|public
name|DocMaker
parameter_list|()
block|{   }
comment|// create a doc
comment|// use only part of the body, modify it to keep the rest (or use all if size==0).
comment|// reset the docdata properties so they are not added more than once.
DECL|method|createDocument
specifier|private
name|Document
name|createDocument
parameter_list|(
name|DocData
name|docData
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|cnt
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
specifier|final
name|DocState
name|ds
init|=
name|getDocState
argument_list|()
decl_stmt|;
specifier|final
name|Document
name|doc
init|=
name|reuseFields
condition|?
name|ds
operator|.
name|doc
else|:
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// Set ID_FIELD
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|valType
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Field
name|idField
init|=
name|ds
operator|.
name|getField
argument_list|(
name|ID_FIELD
argument_list|,
name|ft
argument_list|)
decl_stmt|;
name|int
name|id
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|id
operator|=
name|r
operator|.
name|nextInt
argument_list|(
name|updateDocIDLimit
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|id
operator|=
name|docData
operator|.
name|getID
argument_list|()
expr_stmt|;
if|if
condition|(
name|id
operator|==
operator|-
literal|1
condition|)
block|{
name|id
operator|=
name|numDocsCreated
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
block|}
block|}
name|idField
operator|.
name|setStringValue
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
comment|// Set NAME_FIELD
name|String
name|name
init|=
name|docData
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
name|name
operator|=
literal|""
expr_stmt|;
name|name
operator|=
name|cnt
operator|<
literal|0
condition|?
name|name
else|:
name|name
operator|+
literal|"_"
operator|+
name|cnt
expr_stmt|;
name|Field
name|nameField
init|=
name|ds
operator|.
name|getField
argument_list|(
name|NAME_FIELD
argument_list|,
name|valType
argument_list|)
decl_stmt|;
name|nameField
operator|.
name|setStringValue
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|nameField
argument_list|)
expr_stmt|;
comment|// Set DATE_FIELD
name|DateUtil
name|util
init|=
name|dateParsers
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|util
operator|==
literal|null
condition|)
block|{
name|util
operator|=
operator|new
name|DateUtil
argument_list|()
expr_stmt|;
name|dateParsers
operator|.
name|set
argument_list|(
name|util
argument_list|)
expr_stmt|;
block|}
name|Date
name|date
init|=
literal|null
decl_stmt|;
name|String
name|dateString
init|=
name|docData
operator|.
name|getDate
argument_list|()
decl_stmt|;
if|if
condition|(
name|dateString
operator|!=
literal|null
condition|)
block|{
name|util
operator|.
name|pos
operator|.
name|setIndex
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|date
operator|=
name|util
operator|.
name|parser
operator|.
name|parse
argument_list|(
name|dateString
argument_list|,
name|util
operator|.
name|pos
argument_list|)
expr_stmt|;
comment|//System.out.println(dateString + " parsed to " + date);
block|}
else|else
block|{
name|dateString
operator|=
literal|""
expr_stmt|;
block|}
name|Field
name|dateStringField
init|=
name|ds
operator|.
name|getField
argument_list|(
name|DATE_FIELD
argument_list|,
name|valType
argument_list|)
decl_stmt|;
name|dateStringField
operator|.
name|setStringValue
argument_list|(
name|dateString
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|dateStringField
argument_list|)
expr_stmt|;
if|if
condition|(
name|date
operator|==
literal|null
condition|)
block|{
comment|// just set to right now
name|date
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
block|}
name|Field
name|dateField
init|=
name|ds
operator|.
name|getNumericField
argument_list|(
name|DATE_MSEC_FIELD
argument_list|,
name|NumericType
operator|.
name|LONG
argument_list|)
decl_stmt|;
name|dateField
operator|.
name|setLongValue
argument_list|(
name|date
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|dateField
argument_list|)
expr_stmt|;
name|util
operator|.
name|cal
operator|.
name|setTime
argument_list|(
name|date
argument_list|)
expr_stmt|;
specifier|final
name|int
name|sec
init|=
name|util
operator|.
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|)
operator|*
literal|3600
operator|+
name|util
operator|.
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|)
operator|*
literal|60
operator|+
name|util
operator|.
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|)
decl_stmt|;
name|Field
name|timeSecField
init|=
name|ds
operator|.
name|getNumericField
argument_list|(
name|TIME_SEC_FIELD
argument_list|,
name|NumericType
operator|.
name|INT
argument_list|)
decl_stmt|;
name|timeSecField
operator|.
name|setIntValue
argument_list|(
name|sec
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|timeSecField
argument_list|)
expr_stmt|;
comment|// Set TITLE_FIELD
name|String
name|title
init|=
name|docData
operator|.
name|getTitle
argument_list|()
decl_stmt|;
name|Field
name|titleField
init|=
name|ds
operator|.
name|getField
argument_list|(
name|TITLE_FIELD
argument_list|,
name|valType
argument_list|)
decl_stmt|;
name|titleField
operator|.
name|setStringValue
argument_list|(
name|title
operator|==
literal|null
condition|?
literal|""
else|:
name|title
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|titleField
argument_list|)
expr_stmt|;
name|String
name|body
init|=
name|docData
operator|.
name|getBody
argument_list|()
decl_stmt|;
if|if
condition|(
name|body
operator|!=
literal|null
operator|&&
name|body
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|bdy
decl_stmt|;
if|if
condition|(
name|size
operator|<=
literal|0
operator|||
name|size
operator|>=
name|body
operator|.
name|length
argument_list|()
condition|)
block|{
name|bdy
operator|=
name|body
expr_stmt|;
comment|// use all
name|docData
operator|.
name|setBody
argument_list|(
literal|""
argument_list|)
expr_stmt|;
comment|// nothing left
block|}
else|else
block|{
comment|// attempt not to break words - if whitespace found within next 20 chars...
for|for
control|(
name|int
name|n
init|=
name|size
operator|-
literal|1
init|;
name|n
operator|<
name|size
operator|+
literal|20
operator|&&
name|n
operator|<
name|body
operator|.
name|length
argument_list|()
condition|;
name|n
operator|++
control|)
block|{
if|if
condition|(
name|Character
operator|.
name|isWhitespace
argument_list|(
name|body
operator|.
name|charAt
argument_list|(
name|n
argument_list|)
argument_list|)
condition|)
block|{
name|size
operator|=
name|n
expr_stmt|;
break|break;
block|}
block|}
name|bdy
operator|=
name|body
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
comment|// use part
name|docData
operator|.
name|setBody
argument_list|(
name|body
operator|.
name|substring
argument_list|(
name|size
argument_list|)
argument_list|)
expr_stmt|;
comment|// some left
block|}
name|Field
name|bodyField
init|=
name|ds
operator|.
name|getField
argument_list|(
name|BODY_FIELD
argument_list|,
name|bodyValType
argument_list|)
decl_stmt|;
name|bodyField
operator|.
name|setStringValue
argument_list|(
name|bdy
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|bodyField
argument_list|)
expr_stmt|;
if|if
condition|(
name|storeBytes
condition|)
block|{
name|Field
name|bytesField
init|=
name|ds
operator|.
name|getField
argument_list|(
name|BYTES_FIELD
argument_list|,
name|StringField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|bytesField
operator|.
name|setBytesValue
argument_list|(
name|bdy
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|bytesField
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|indexProperties
condition|)
block|{
name|Properties
name|props
init|=
name|docData
operator|.
name|getProps
argument_list|()
decl_stmt|;
if|if
condition|(
name|props
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|props
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Field
name|f
init|=
name|ds
operator|.
name|getField
argument_list|(
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|valType
argument_list|)
decl_stmt|;
name|f
operator|.
name|setStringValue
argument_list|(
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
name|docData
operator|.
name|setProps
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|//System.out.println("============== Created doc "+numDocsCreated+" :\n"+doc+"\n==========");
return|return
name|doc
return|;
block|}
DECL|method|resetLeftovers
specifier|private
name|void
name|resetLeftovers
parameter_list|()
block|{
name|leftovr
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|getDocState
specifier|protected
name|DocState
name|getDocState
parameter_list|()
block|{
name|DocState
name|ds
init|=
name|docState
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|ds
operator|==
literal|null
condition|)
block|{
name|ds
operator|=
operator|new
name|DocState
argument_list|(
name|reuseFields
argument_list|,
name|valType
argument_list|,
name|bodyValType
argument_list|)
expr_stmt|;
name|docState
operator|.
name|set
argument_list|(
name|ds
argument_list|)
expr_stmt|;
block|}
return|return
name|ds
return|;
block|}
comment|/**    * Closes the {@link DocMaker}. The base implementation closes the    * {@link ContentSource}, and it can be overridden to do more work (but make    * sure to call super.close()).    */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|source
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates a {@link Document} object ready for indexing. This method uses the    * {@link ContentSource} to get the next document from the source, and creates    * a {@link Document} object from the returned fields. If    *<code>reuseFields</code> was set to true, it will reuse {@link Document}    * and {@link Field} instances.    */
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|resetLeftovers
argument_list|()
expr_stmt|;
name|DocData
name|docData
init|=
name|source
operator|.
name|getNextDocData
argument_list|(
name|getDocState
argument_list|()
operator|.
name|docData
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|createDocument
argument_list|(
name|docData
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
return|return
name|doc
return|;
block|}
comment|/**    * Same as {@link #makeDocument()}, only this method creates a document of the    * given size input by<code>size</code>.    */
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|Exception
block|{
name|LeftOver
name|lvr
init|=
name|leftovr
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|lvr
operator|==
literal|null
operator|||
name|lvr
operator|.
name|docdata
operator|==
literal|null
operator|||
name|lvr
operator|.
name|docdata
operator|.
name|getBody
argument_list|()
operator|==
literal|null
operator|||
name|lvr
operator|.
name|docdata
operator|.
name|getBody
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|resetLeftovers
argument_list|()
expr_stmt|;
block|}
name|DocData
name|docData
init|=
name|getDocState
argument_list|()
operator|.
name|docData
decl_stmt|;
name|DocData
name|dd
init|=
operator|(
name|lvr
operator|==
literal|null
condition|?
name|source
operator|.
name|getNextDocData
argument_list|(
name|docData
argument_list|)
else|:
name|lvr
operator|.
name|docdata
operator|)
decl_stmt|;
name|int
name|cnt
init|=
operator|(
name|lvr
operator|==
literal|null
condition|?
literal|0
else|:
name|lvr
operator|.
name|cnt
operator|)
decl_stmt|;
while|while
condition|(
name|dd
operator|.
name|getBody
argument_list|()
operator|==
literal|null
operator|||
name|dd
operator|.
name|getBody
argument_list|()
operator|.
name|length
argument_list|()
operator|<
name|size
condition|)
block|{
name|DocData
name|dd2
init|=
name|dd
decl_stmt|;
name|dd
operator|=
name|source
operator|.
name|getNextDocData
argument_list|(
operator|new
name|DocData
argument_list|()
argument_list|)
expr_stmt|;
name|cnt
operator|=
literal|0
expr_stmt|;
name|dd
operator|.
name|setBody
argument_list|(
name|dd2
operator|.
name|getBody
argument_list|()
operator|+
name|dd
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Document
name|doc
init|=
name|createDocument
argument_list|(
name|dd
argument_list|,
name|size
argument_list|,
name|cnt
argument_list|)
decl_stmt|;
if|if
condition|(
name|dd
operator|.
name|getBody
argument_list|()
operator|==
literal|null
operator|||
name|dd
operator|.
name|getBody
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|resetLeftovers
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|lvr
operator|==
literal|null
condition|)
block|{
name|lvr
operator|=
operator|new
name|LeftOver
argument_list|()
expr_stmt|;
name|leftovr
operator|.
name|set
argument_list|(
name|lvr
argument_list|)
expr_stmt|;
block|}
name|lvr
operator|.
name|docdata
operator|=
name|dd
expr_stmt|;
name|lvr
operator|.
name|cnt
operator|=
operator|++
name|cnt
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
comment|/** Reset inputs so that the test run would behave, input wise, as if it just started. */
DECL|method|resetInputs
specifier|public
specifier|synchronized
name|void
name|resetInputs
parameter_list|()
throws|throws
name|IOException
block|{
name|source
operator|.
name|printStatistics
argument_list|(
literal|"docs"
argument_list|)
expr_stmt|;
comment|// re-initiate since properties by round may have changed.
name|setConfig
argument_list|(
name|config
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|source
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
name|numDocsCreated
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|resetLeftovers
argument_list|()
expr_stmt|;
block|}
comment|/** Set the configuration parameters of this doc maker. */
DECL|method|setConfig
specifier|public
name|void
name|setConfig
parameter_list|(
name|Config
name|config
parameter_list|,
name|ContentSource
name|source
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|boolean
name|stored
init|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.stored"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|bodyStored
init|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.body.stored"
argument_list|,
name|stored
argument_list|)
decl_stmt|;
name|boolean
name|tokenized
init|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.tokenized"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|boolean
name|bodyTokenized
init|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.body.tokenized"
argument_list|,
name|tokenized
argument_list|)
decl_stmt|;
name|boolean
name|norms
init|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.tokenized.norms"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|bodyNorms
init|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.body.tokenized.norms"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|boolean
name|termVec
init|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.term.vector"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|termVecPositions
init|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.term.vector.positions"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|termVecOffsets
init|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.term.vector.offsets"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|valType
operator|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
expr_stmt|;
name|valType
operator|.
name|setStored
argument_list|(
name|stored
argument_list|)
expr_stmt|;
name|valType
operator|.
name|setTokenized
argument_list|(
name|tokenized
argument_list|)
expr_stmt|;
name|valType
operator|.
name|setOmitNorms
argument_list|(
operator|!
name|norms
argument_list|)
expr_stmt|;
name|valType
operator|.
name|setStoreTermVectors
argument_list|(
name|termVec
argument_list|)
expr_stmt|;
name|valType
operator|.
name|setStoreTermVectorPositions
argument_list|(
name|termVecPositions
argument_list|)
expr_stmt|;
name|valType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
name|termVecOffsets
argument_list|)
expr_stmt|;
name|valType
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|bodyValType
operator|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
expr_stmt|;
name|bodyValType
operator|.
name|setStored
argument_list|(
name|bodyStored
argument_list|)
expr_stmt|;
name|bodyValType
operator|.
name|setTokenized
argument_list|(
name|bodyTokenized
argument_list|)
expr_stmt|;
name|bodyValType
operator|.
name|setOmitNorms
argument_list|(
operator|!
name|bodyNorms
argument_list|)
expr_stmt|;
name|bodyValType
operator|.
name|setStoreTermVectors
argument_list|(
name|termVec
argument_list|)
expr_stmt|;
name|bodyValType
operator|.
name|setStoreTermVectorPositions
argument_list|(
name|termVecPositions
argument_list|)
expr_stmt|;
name|bodyValType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
name|termVecOffsets
argument_list|)
expr_stmt|;
name|bodyValType
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|storeBytes
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.store.body.bytes"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|reuseFields
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.reuse.fields"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// In a multi-rounds run, it is important to reset DocState since settings
comment|// of fields may change between rounds, and this is the only way to reset
comment|// the cache of all threads.
name|docState
operator|=
operator|new
name|ThreadLocal
argument_list|<>
argument_list|()
expr_stmt|;
name|indexProperties
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.index.props"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|updateDocIDLimit
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.random.id.limit"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|updateDocIDLimit
operator|!=
operator|-
literal|1
condition|)
block|{
name|r
operator|=
operator|new
name|Random
argument_list|(
literal|179
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
