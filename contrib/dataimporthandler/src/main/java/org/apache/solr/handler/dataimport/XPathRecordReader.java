begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLInputFactory
import|;
end_import
begin_import
import|import static
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamConstants
operator|.
name|*
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
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
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import
begin_comment
comment|/**  *<p>  * A streaming xpath parser which uses StAX for XML parsing. It supports only a  * subset of xpath syntax.  *</p>  * /a/b/subject[@qualifier='fullTitle']  * /a/b/subject/@qualifier  * /a/b/c  *  * Keep in mind that the wild-card syntax  '//' is not supported  *  *<p/>  *<b>This API is experimental and may change in the future.</b>  * This class is thread-safe for parsing xml . But adding fields is not thread-safe. The recommended usage is  * to addField() in one thread and then share the instance across threads.  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|XPathRecordReader
specifier|public
class|class
name|XPathRecordReader
block|{
DECL|field|rootNode
specifier|private
name|Node
name|rootNode
init|=
operator|new
name|Node
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|/**Use this flag in the addField() method to fetch all the cdata under a specific tag    *    */
DECL|field|FLATTEN
specifier|public
specifier|static
specifier|final
name|int
name|FLATTEN
init|=
literal|1
decl_stmt|;
comment|/**    * @param forEachXpath  The XPATH for which a record is emitted. At the start of this xpath tag, it starts collecting the fields and at the close    * of the tag ,a record is emitted and the fields collected since the tag start is included in the record. If there    * are fields collected in the parent tag(s) they also will be included in the record but not cleared after emitting the record.    * It can use the ' | ' syntax of XPATH to pass in multiple xpaths.    */
DECL|method|XPathRecordReader
specifier|public
name|XPathRecordReader
parameter_list|(
name|String
name|forEachXpath
parameter_list|)
block|{
name|String
index|[]
name|splits
init|=
name|forEachXpath
operator|.
name|split
argument_list|(
literal|"\\|"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|split
range|:
name|splits
control|)
block|{
name|split
operator|=
name|split
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|split
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
continue|continue;
name|addField0
argument_list|(
name|split
argument_list|,
name|split
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addField
specifier|public
specifier|synchronized
name|XPathRecordReader
name|addField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|xpath
parameter_list|,
name|boolean
name|multiValued
parameter_list|)
block|{
if|if
condition|(
operator|!
name|xpath
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"xpath must start with '/' : "
operator|+
name|xpath
argument_list|)
throw|;
name|addField0
argument_list|(
name|xpath
argument_list|,
name|name
argument_list|,
name|multiValued
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**Add a field's XPATH and its name.    * @param name . The name by which this field is referred in the emitted record    * @param xpath . The xpath  to this field    * @param multiValued . If this is 'true' , then the emitted record will have a List<String> as value    * @param flags . The only supported flag is 'FLATTEN'    */
DECL|method|addField
specifier|public
specifier|synchronized
name|XPathRecordReader
name|addField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|xpath
parameter_list|,
name|boolean
name|multiValued
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
if|if
condition|(
operator|!
name|xpath
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"xpath must start with '/' : "
operator|+
name|xpath
argument_list|)
throw|;
name|addField0
argument_list|(
name|xpath
argument_list|,
name|name
argument_list|,
name|multiValued
argument_list|,
literal|false
argument_list|,
name|flags
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addField0
specifier|private
name|void
name|addField0
parameter_list|(
name|String
name|xpath
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|multiValued
parameter_list|,
name|boolean
name|isRecord
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|splitEscapeQuote
argument_list|(
name|xpath
argument_list|)
decl_stmt|;
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|paths
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
condition|)
name|paths
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|rootNode
operator|.
name|build
argument_list|(
name|paths
argument_list|,
name|name
argument_list|,
name|multiValued
argument_list|,
name|isRecord
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
DECL|method|getAllRecords
specifier|public
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|getAllRecords
parameter_list|(
name|Reader
name|r
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|streamRecords
argument_list|(
name|r
argument_list|,
operator|new
name|Handler
argument_list|()
block|{
specifier|public
name|void
name|handle
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
parameter_list|,
name|String
name|s
parameter_list|)
block|{
name|results
operator|.
name|add
argument_list|(
name|record
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|results
return|;
block|}
comment|/** Stream records as and when they are colected    * @param r The reader    * @param handler The callback instance    */
DECL|method|streamRecords
specifier|public
name|void
name|streamRecords
parameter_list|(
name|Reader
name|r
parameter_list|,
name|Handler
name|handler
parameter_list|)
block|{
try|try
block|{
name|XMLStreamReader
name|parser
init|=
name|factory
operator|.
name|createXMLStreamReader
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|rootNode
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
name|handler
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|,
operator|new
name|Stack
argument_list|<
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**For each node/leaf in the tree there is one object of this class    */
DECL|class|Node
specifier|private
class|class
name|Node
block|{
comment|/**name of the tag/attribute*/
DECL|field|name
name|String
name|name
decl_stmt|;
comment|/**The field name as passed in the addField() . This will be used in the record*/
DECL|field|fieldName
name|String
name|fieldName
decl_stmt|;
comment|/**stores the xpath name such as '@attr='xyz'*/
DECL|field|xpathName
name|String
name|xpathName
decl_stmt|;
comment|/**The xpath of the record. if this is a record node */
DECL|field|forEachPath
name|String
name|forEachPath
decl_stmt|;
comment|/**child attribute nodes */
DECL|field|attributes
name|List
argument_list|<
name|Node
argument_list|>
name|attributes
decl_stmt|;
comment|/**child nodes*/
DECL|field|childNodes
name|List
argument_list|<
name|Node
argument_list|>
name|childNodes
decl_stmt|;
comment|/**if attribs are used in the xpath their names and values*/
DECL|field|attribAndValues
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|attribAndValues
decl_stmt|;
comment|/**Parent node of this node */
DECL|field|parent
name|Node
name|parent
decl_stmt|;
DECL|field|hasText
DECL|field|multiValued
DECL|field|isRecord
name|boolean
name|hasText
init|=
literal|false
decl_stmt|,
name|multiValued
init|=
literal|false
decl_stmt|,
name|isRecord
init|=
literal|false
decl_stmt|;
DECL|field|flatten
specifier|private
name|boolean
name|flatten
decl_stmt|;
DECL|method|Node
specifier|public
name|Node
parameter_list|(
name|String
name|name
parameter_list|,
name|Node
name|p
parameter_list|)
block|{
name|xpathName
operator|=
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|parent
operator|=
name|p
expr_stmt|;
block|}
DECL|method|Node
specifier|public
name|Node
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|boolean
name|multiValued
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|multiValued
operator|=
name|multiValued
expr_stmt|;
block|}
comment|/**This is the method where all the parsing happens. For each tag/subtag this gets called recursively.      */
DECL|method|parse
specifier|private
name|void
name|parse
parameter_list|(
name|XMLStreamReader
name|parser
parameter_list|,
name|Handler
name|handler
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|values
parameter_list|,
name|Stack
argument_list|<
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|stack
parameter_list|,
name|boolean
name|recordStarted
parameter_list|)
throws|throws
name|IOException
throws|,
name|XMLStreamException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|valuesAddedinThisFrame
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isRecord
condition|)
block|{
name|recordStarted
operator|=
literal|true
expr_stmt|;
name|valuesAddedinThisFrame
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|stack
operator|.
name|push
argument_list|(
name|valuesAddedinThisFrame
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|recordStarted
condition|)
block|{
name|valuesAddedinThisFrame
operator|=
name|stack
operator|.
name|peek
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|attributes
operator|!=
literal|null
operator|||
name|hasText
condition|)
name|valuesAddedinThisFrame
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|stack
operator|.
name|push
argument_list|(
name|valuesAddedinThisFrame
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|attributes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Node
name|node
range|:
name|attributes
control|)
block|{
name|String
name|value
init|=
name|parser
operator|.
name|getAttributeValue
argument_list|(
literal|null
argument_list|,
name|node
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
operator|||
operator|(
name|recordStarted
operator|&&
operator|!
name|isRecord
operator|)
condition|)
block|{
name|putText
argument_list|(
name|values
argument_list|,
name|value
argument_list|,
name|node
operator|.
name|fieldName
argument_list|,
name|node
operator|.
name|multiValued
argument_list|)
expr_stmt|;
name|valuesAddedinThisFrame
operator|.
name|add
argument_list|(
name|node
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Set
argument_list|<
name|Node
argument_list|>
name|childrenFound
init|=
operator|new
name|HashSet
argument_list|<
name|Node
argument_list|>
argument_list|()
decl_stmt|;
comment|// for any normal event , parser.next() should be called in each iteration.
comment|// But for CDATA | CHARACTERS | SPACE it should not do so because handling of
comment|// CDATA itself would have consumed the next event. CDATA may throw multiple events
comment|// so all the events are slurped till a  START_ELEMENT is encountered.
name|boolean
name|isNextEventFetched
init|=
literal|false
decl_stmt|;
name|int
name|event
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
operator|!
name|isNextEventFetched
condition|)
block|{
name|event
operator|=
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
name|isNextEventFetched
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|event
operator|==
name|END_DOCUMENT
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|event
operator|==
name|END_ELEMENT
condition|)
block|{
if|if
condition|(
name|isRecord
condition|)
name|handler
operator|.
name|handle
argument_list|(
name|getDeepCopy
argument_list|(
name|values
argument_list|)
argument_list|,
name|forEachPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|recordStarted
operator|&&
operator|!
name|isRecord
operator|&&
operator|!
name|childrenFound
operator|.
name|containsAll
argument_list|(
name|childNodes
argument_list|)
condition|)
block|{
for|for
control|(
name|Node
name|n
range|:
name|childNodes
control|)
block|{
if|if
condition|(
operator|!
name|childrenFound
operator|.
name|contains
argument_list|(
name|n
argument_list|)
condition|)
name|n
operator|.
name|putNulls
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
block|}
return|return;
block|}
if|if
condition|(
operator|(
name|event
operator|==
name|CDATA
operator|||
name|event
operator|==
name|CHARACTERS
operator|||
name|event
operator|==
name|SPACE
operator|)
operator|&&
name|hasText
condition|)
block|{
name|valuesAddedinThisFrame
operator|.
name|add
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|isNextEventFetched
operator|=
literal|true
expr_stmt|;
name|String
name|text
init|=
name|parser
operator|.
name|getText
argument_list|()
decl_stmt|;
name|event
operator|=
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|event
operator|==
name|CDATA
operator|||
name|event
operator|==
name|CHARACTERS
operator|||
name|event
operator|==
name|SPACE
condition|)
block|{
name|text
operator|=
name|text
operator|+
name|parser
operator|.
name|getText
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|event
operator|==
name|START_ELEMENT
condition|)
block|{
if|if
condition|(
name|flatten
condition|)
block|{
name|int
name|starts
init|=
literal|1
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|event
operator|=
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|event
operator|==
name|CDATA
operator|||
name|event
operator|==
name|CHARACTERS
operator|||
name|event
operator|==
name|SPACE
condition|)
block|{
name|text
operator|=
name|text
operator|+
name|parser
operator|.
name|getText
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|event
operator|==
name|START_ELEMENT
condition|)
block|{
name|starts
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|event
operator|==
name|END_ELEMENT
condition|)
block|{
name|starts
operator|--
expr_stmt|;
if|if
condition|(
name|starts
operator|==
literal|0
condition|)
break|break;
block|}
block|}
block|}
else|else
block|{
name|handleStartElement
argument_list|(
name|parser
argument_list|,
name|childrenFound
argument_list|,
name|handler
argument_list|,
name|values
argument_list|,
name|stack
argument_list|,
name|recordStarted
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
break|break;
block|}
name|event
operator|=
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|putText
argument_list|(
name|values
argument_list|,
name|text
argument_list|,
name|fieldName
argument_list|,
name|multiValued
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|event
operator|==
name|START_ELEMENT
condition|)
block|{
name|handleStartElement
argument_list|(
name|parser
argument_list|,
name|childrenFound
argument_list|,
name|handler
argument_list|,
name|values
argument_list|,
name|stack
argument_list|,
name|recordStarted
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
comment|/*If a record has ended  (tag closed) then clearup all the fields found         in this record after this tag started */
name|Set
argument_list|<
name|String
argument_list|>
name|cleanThis
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isRecord
operator|||
operator|!
name|recordStarted
condition|)
block|{
name|cleanThis
operator|=
name|stack
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
return|return;
block|}
if|if
condition|(
name|cleanThis
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|fld
range|:
name|cleanThis
control|)
block|{
name|values
operator|.
name|remove
argument_list|(
name|fld
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**if a new tag is encountered, check if it is of interest of not (if there is a matching child Node).      * if yes continue parsing else skip      */
DECL|method|handleStartElement
specifier|private
name|void
name|handleStartElement
parameter_list|(
name|XMLStreamReader
name|parser
parameter_list|,
name|Set
argument_list|<
name|Node
argument_list|>
name|childrenFound
parameter_list|,
name|Handler
name|handler
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|values
parameter_list|,
name|Stack
argument_list|<
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|stack
parameter_list|,
name|boolean
name|recordStarted
parameter_list|)
throws|throws
name|IOException
throws|,
name|XMLStreamException
block|{
name|Node
name|n
init|=
name|getMatchingChild
argument_list|(
name|parser
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
block|{
name|childrenFound
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|n
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
name|handler
argument_list|,
name|values
argument_list|,
name|stack
argument_list|,
name|recordStarted
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|skipTag
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**check if the current tag is to be parsed or not. if yes return the Node object      */
DECL|method|getMatchingChild
specifier|private
name|Node
name|getMatchingChild
parameter_list|(
name|XMLStreamReader
name|parser
parameter_list|)
block|{
if|if
condition|(
name|childNodes
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|String
name|localName
init|=
name|parser
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
for|for
control|(
name|Node
name|n
range|:
name|childNodes
control|)
block|{
if|if
condition|(
name|n
operator|.
name|name
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
if|if
condition|(
name|n
operator|.
name|attribAndValues
operator|==
literal|null
condition|)
return|return
name|n
return|;
if|if
condition|(
name|checkForAttributes
argument_list|(
name|parser
argument_list|,
name|n
operator|.
name|attribAndValues
argument_list|)
condition|)
return|return
name|n
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|checkForAttributes
specifier|private
name|boolean
name|checkForAttributes
parameter_list|(
name|XMLStreamReader
name|parser
parameter_list|,
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|attrs
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|e
range|:
name|attrs
control|)
block|{
name|String
name|val
init|=
name|parser
operator|.
name|getAttributeValue
argument_list|(
literal|null
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|val
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**If there is no value available for a field in a subtag then add a null      * TODO : needs better explanation      */
DECL|method|putNulls
specifier|private
name|void
name|putNulls
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|values
parameter_list|)
block|{
if|if
condition|(
name|attributes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Node
name|n
range|:
name|attributes
control|)
block|{
if|if
condition|(
name|n
operator|.
name|multiValued
condition|)
name|putText
argument_list|(
name|values
argument_list|,
literal|null
argument_list|,
name|n
operator|.
name|fieldName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|hasText
operator|&&
name|multiValued
condition|)
name|putText
argument_list|(
name|values
argument_list|,
literal|null
argument_list|,
name|fieldName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|childNodes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Node
name|childNode
range|:
name|childNodes
control|)
name|childNode
operator|.
name|putNulls
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**Handle multivalued fields by adding List<String>      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|putText
specifier|private
name|void
name|putText
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|values
parameter_list|,
name|String
name|value
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|boolean
name|multiValued
parameter_list|)
block|{
if|if
condition|(
name|multiValued
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|v
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|values
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|v
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
name|v
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|values
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**Skip a tag w/o processing the tag or its subtags      */
DECL|method|skipTag
specifier|private
name|void
name|skipTag
parameter_list|(
name|XMLStreamReader
name|parser
parameter_list|)
throws|throws
name|IOException
throws|,
name|XMLStreamException
block|{
name|int
name|type
decl_stmt|;
while|while
condition|(
operator|(
name|type
operator|=
name|parser
operator|.
name|next
argument_list|()
operator|)
operator|!=
name|END_ELEMENT
condition|)
block|{
if|if
condition|(
name|type
operator|==
name|START_ELEMENT
condition|)
name|skipTag
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**Build the node structure from the xpath      * @param paths the xpaths split by '/'      * @param fieldName name of the field      * @param multiValued . is multiValued or not      * @param record is this xpath a record or a field      * @param flags extra flags      */
DECL|method|build
specifier|private
name|void
name|build
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|boolean
name|multiValued
parameter_list|,
name|boolean
name|record
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|String
name|name
init|=
name|paths
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|paths
operator|.
name|isEmpty
argument_list|()
operator|&&
name|name
operator|.
name|startsWith
argument_list|(
literal|"@"
argument_list|)
condition|)
block|{
if|if
condition|(
name|attributes
operator|==
literal|null
condition|)
block|{
name|attributes
operator|=
operator|new
name|ArrayList
argument_list|<
name|Node
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|attributes
operator|.
name|add
argument_list|(
operator|new
name|Node
argument_list|(
name|name
argument_list|,
name|fieldName
argument_list|,
name|multiValued
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|childNodes
operator|==
literal|null
condition|)
name|childNodes
operator|=
operator|new
name|ArrayList
argument_list|<
name|Node
argument_list|>
argument_list|()
expr_stmt|;
name|Node
name|n
init|=
name|getOrAddChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|paths
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|record
condition|)
block|{
name|n
operator|.
name|isRecord
operator|=
literal|true
expr_stmt|;
name|n
operator|.
name|forEachPath
operator|=
name|fieldName
expr_stmt|;
block|}
else|else
block|{
name|n
operator|.
name|hasText
operator|=
literal|true
expr_stmt|;
name|n
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|n
operator|.
name|multiValued
operator|=
name|multiValued
expr_stmt|;
name|n
operator|.
name|flatten
operator|=
name|flags
operator|==
name|FLATTEN
expr_stmt|;
block|}
block|}
else|else
block|{
name|n
operator|.
name|build
argument_list|(
name|paths
argument_list|,
name|fieldName
argument_list|,
name|multiValued
argument_list|,
name|record
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getOrAddChildNode
specifier|private
name|Node
name|getOrAddChildNode
parameter_list|(
name|String
name|xpathName
parameter_list|)
block|{
for|for
control|(
name|Node
name|n
range|:
name|childNodes
control|)
if|if
condition|(
name|n
operator|.
name|xpathName
operator|.
name|equals
argument_list|(
name|xpathName
argument_list|)
condition|)
return|return
name|n
return|;
name|Node
name|n
init|=
operator|new
name|Node
argument_list|(
name|xpathName
argument_list|,
name|this
argument_list|)
decl_stmt|;
name|Matcher
name|m
init|=
name|ATTRIB_PRESENT_WITHVAL
operator|.
name|matcher
argument_list|(
name|xpathName
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|n
operator|.
name|name
operator|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|int
name|start
init|=
name|m
operator|.
name|start
argument_list|(
literal|2
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attribs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|find
argument_list|(
name|start
argument_list|)
condition|)
break|break;
name|attribs
operator|.
name|put
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|start
operator|=
name|m
operator|.
name|end
argument_list|(
literal|6
argument_list|)
expr_stmt|;
if|if
condition|(
name|n
operator|.
name|attribAndValues
operator|==
literal|null
condition|)
name|n
operator|.
name|attribAndValues
operator|=
operator|new
name|ArrayList
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|n
operator|.
name|attribAndValues
operator|.
name|addAll
argument_list|(
name|attribs
operator|.
name|entrySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|childNodes
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
return|return
name|n
return|;
block|}
block|}
comment|/**If a field has List then they have to be deep-copied for thread safety    */
DECL|method|getDeepCopy
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getDeepCopy
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|values
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|values
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|instanceof
name|List
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|(
operator|(
name|List
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**    * Used for handling cases where there is a slash '/' character    * inside the attribute value e.g. x@html='text/html'. We need to split    * by '/' excluding the '/' which is a part of the attribute's value.    */
DECL|method|splitEscapeQuote
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|splitEscapeQuote
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
index|[]
name|ss
init|=
name|str
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ss
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|ss
index|[
name|i
index|]
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|&&
name|result
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
continue|continue;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|quoteCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|ss
index|[
name|i
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|ss
index|[
name|i
index|]
operator|.
name|length
argument_list|()
condition|;
name|j
operator|++
control|)
if|if
condition|(
name|ss
index|[
name|i
index|]
operator|.
name|charAt
argument_list|(
name|j
argument_list|)
operator|==
literal|'\''
condition|)
name|quoteCount
operator|++
expr_stmt|;
if|if
condition|(
operator|(
name|quoteCount
operator|%
literal|2
operator|)
operator|==
literal|0
condition|)
break|break;
name|i
operator|++
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|field|factory
specifier|static
name|XMLInputFactory
name|factory
init|=
name|XMLInputFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
static|static
block|{
name|factory
operator|.
name|setProperty
argument_list|(
name|XMLInputFactory
operator|.
name|IS_VALIDATING
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setProperty
argument_list|(
name|XMLInputFactory
operator|.
name|SUPPORT_DTD
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
comment|/**Implement this interface to stream records as and when it is found.    *    */
DECL|interface|Handler
specifier|public
specifier|static
interface|interface
name|Handler
block|{
comment|/**      * @param record The record map . The key is the field name as provided in the addField() methods. The value      * can be a single String (for single valued) or a List<String> (for multiValued)      * if an Exception is thrown from this method the parsing will be aborted      * @param xpath . The forEach XPATH for which this record is being emitted      */
DECL|method|handle
specifier|public
name|void
name|handle
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
parameter_list|,
name|String
name|xpath
parameter_list|)
function_decl|;
block|}
DECL|field|ATTRIB_PRESENT_WITHVAL
specifier|private
specifier|static
specifier|final
name|Pattern
name|ATTRIB_PRESENT_WITHVAL
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(\\S*?)?(\\[@)(\\S*?)(='(.*?)')?(\\])"
argument_list|)
decl_stmt|;
block|}
end_class
end_unit
