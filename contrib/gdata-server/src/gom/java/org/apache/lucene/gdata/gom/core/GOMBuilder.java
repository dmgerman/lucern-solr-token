begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.gom.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|gom
operator|.
name|core
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
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
name|XMLStreamConstants
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|gom
operator|.
name|GOMDocument
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
name|gdata
operator|.
name|gom
operator|.
name|GOMEntry
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
name|gdata
operator|.
name|gom
operator|.
name|GOMFeed
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
name|gdata
operator|.
name|gom
operator|.
name|GOMNamespace
import|;
end_import
begin_comment
comment|/**  * @author Simon Willnauer  */
end_comment
begin_class
DECL|class|GOMBuilder
specifier|public
class|class
name|GOMBuilder
block|{
DECL|field|streamReader
specifier|private
specifier|final
name|XMLStreamReader
name|streamReader
decl_stmt|;
DECL|field|factory
specifier|private
specifier|final
name|GOMFactory
name|factory
decl_stmt|;
DECL|field|parserStack
specifier|private
specifier|final
name|Stack
argument_list|<
name|AtomParser
argument_list|>
name|parserStack
decl_stmt|;
comment|/** 	 * @param arg0 	 */
DECL|method|GOMBuilder
specifier|public
name|GOMBuilder
parameter_list|(
name|XMLStreamReader
name|arg0
parameter_list|)
block|{
if|if
condition|(
name|arg0
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"XMLStreamReader instance must not be null"
argument_list|)
throw|;
name|this
operator|.
name|streamReader
operator|=
name|arg0
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|GOMFactory
operator|.
name|createInstance
argument_list|()
expr_stmt|;
name|this
operator|.
name|parserStack
operator|=
operator|new
name|Stack
argument_list|<
name|AtomParser
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|buildGOMFeed
specifier|public
name|GOMDocument
argument_list|<
name|GOMFeed
argument_list|>
name|buildGOMFeed
parameter_list|()
throws|throws
name|XMLStreamException
block|{
name|GOMDocument
argument_list|<
name|GOMFeed
argument_list|>
name|document
init|=
operator|new
name|GOMDocumentImpl
argument_list|<
name|GOMFeed
argument_list|>
argument_list|()
decl_stmt|;
name|GOMFeed
name|element
init|=
name|startFeedDocument
argument_list|(
name|document
argument_list|)
decl_stmt|;
name|document
operator|.
name|setRootElement
argument_list|(
name|element
argument_list|)
expr_stmt|;
name|parse
argument_list|(
name|this
operator|.
name|streamReader
argument_list|)
expr_stmt|;
return|return
name|document
return|;
block|}
DECL|method|parse
specifier|private
name|void
name|parse
parameter_list|(
name|XMLStreamReader
name|aReader
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|int
name|next
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|next
operator|=
name|next
argument_list|()
operator|)
operator|!=
name|XMLStreamConstants
operator|.
name|END_DOCUMENT
condition|)
block|{
if|if
condition|(
name|next
operator|==
name|XMLStreamConstants
operator|.
name|START_ELEMENT
condition|)
block|{
name|AtomParser
name|childParser
init|=
name|this
operator|.
name|parserStack
operator|.
name|peek
argument_list|()
operator|.
name|getChildParser
argument_list|(
name|this
operator|.
name|streamReader
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|processAttributes
argument_list|(
name|childParser
argument_list|)
expr_stmt|;
name|this
operator|.
name|parserStack
operator|.
name|push
argument_list|(
name|childParser
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|next
operator|==
name|XMLStreamConstants
operator|.
name|END_ELEMENT
condition|)
block|{
name|this
operator|.
name|parserStack
operator|.
name|pop
argument_list|()
operator|.
name|processEndElement
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|next
operator|==
name|XMLStreamConstants
operator|.
name|CHARACTERS
condition|)
block|{
name|this
operator|.
name|parserStack
operator|.
name|peek
argument_list|()
operator|.
name|processElementValue
argument_list|(
name|this
operator|.
name|streamReader
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|next
operator|==
name|XMLStreamConstants
operator|.
name|CDATA
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"CDdata"
argument_list|)
expr_stmt|;
block|}
comment|// System.out.println(next);
block|}
block|}
comment|/** 	 * @param childParser 	 */
DECL|method|processAttributes
specifier|private
name|void
name|processAttributes
parameter_list|(
name|AtomParser
name|childParser
parameter_list|)
block|{
name|int
name|attributeCount
init|=
name|this
operator|.
name|streamReader
operator|.
name|getAttributeCount
argument_list|()
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
name|attributeCount
condition|;
name|i
operator|++
control|)
block|{
name|childParser
operator|.
name|processAttribute
argument_list|(
name|this
operator|.
name|streamReader
operator|.
name|getAttributeName
argument_list|(
name|i
argument_list|)
argument_list|,
name|this
operator|.
name|streamReader
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|buildGOMEntry
specifier|public
name|GOMDocument
argument_list|<
name|GOMEntry
argument_list|>
name|buildGOMEntry
parameter_list|()
throws|throws
name|XMLStreamException
block|{
name|GOMDocument
argument_list|<
name|GOMEntry
argument_list|>
name|document
init|=
operator|new
name|GOMDocumentImpl
argument_list|<
name|GOMEntry
argument_list|>
argument_list|()
decl_stmt|;
name|GOMEntry
name|element
init|=
name|startEntryDocument
argument_list|(
name|document
argument_list|)
decl_stmt|;
name|document
operator|.
name|setRootElement
argument_list|(
name|element
argument_list|)
expr_stmt|;
name|parse
argument_list|(
name|this
operator|.
name|streamReader
argument_list|)
expr_stmt|;
return|return
name|document
return|;
block|}
DECL|method|startEntryDocument
specifier|private
name|GOMEntry
name|startEntryDocument
parameter_list|(
name|GOMDocument
name|aDocument
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|aDocument
operator|.
name|setVersion
argument_list|(
name|this
operator|.
name|streamReader
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|aDocument
operator|.
name|setCharacterEncoding
argument_list|(
name|this
operator|.
name|streamReader
operator|.
name|getCharacterEncodingScheme
argument_list|()
argument_list|)
expr_stmt|;
name|GOMEntry
name|entry
init|=
name|this
operator|.
name|factory
operator|.
name|createEntry
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
argument_list|()
operator|!=
name|XMLStreamConstants
operator|.
name|START_ELEMENT
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
literal|"Expected start of feed element"
argument_list|)
throw|;
name|processAttributes
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|this
operator|.
name|parserStack
operator|.
name|push
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|this
operator|.
name|streamReader
operator|.
name|getNamespaceCount
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|GOMNamespace
name|namespace
init|=
operator|new
name|GOMNamespace
argument_list|(
name|this
operator|.
name|streamReader
operator|.
name|getNamespaceURI
argument_list|(
name|i
argument_list|)
argument_list|,
name|this
operator|.
name|streamReader
operator|.
name|getNamespacePrefix
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|entry
operator|.
name|addNamespace
argument_list|(
name|namespace
argument_list|)
expr_stmt|;
block|}
return|return
name|entry
return|;
block|}
DECL|method|startFeedDocument
specifier|private
name|GOMFeed
name|startFeedDocument
parameter_list|(
name|GOMDocument
name|aDocument
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|aDocument
operator|.
name|setVersion
argument_list|(
name|this
operator|.
name|streamReader
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|aDocument
operator|.
name|setCharacterEncoding
argument_list|(
name|this
operator|.
name|streamReader
operator|.
name|getCharacterEncodingScheme
argument_list|()
argument_list|)
expr_stmt|;
name|GOMFeed
name|feed
init|=
name|this
operator|.
name|factory
operator|.
name|createFeed
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
argument_list|()
operator|!=
name|XMLStreamConstants
operator|.
name|START_ELEMENT
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
literal|"Expected start of feed element"
argument_list|)
throw|;
name|processAttributes
argument_list|(
name|feed
argument_list|)
expr_stmt|;
name|this
operator|.
name|parserStack
operator|.
name|push
argument_list|(
name|feed
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|this
operator|.
name|streamReader
operator|.
name|getNamespaceCount
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|GOMNamespace
name|namespace
init|=
operator|new
name|GOMNamespace
argument_list|(
name|this
operator|.
name|streamReader
operator|.
name|getNamespaceURI
argument_list|(
name|i
argument_list|)
argument_list|,
name|this
operator|.
name|streamReader
operator|.
name|getNamespacePrefix
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|namespace
argument_list|)
expr_stmt|;
name|feed
operator|.
name|addNamespace
argument_list|(
name|namespace
argument_list|)
expr_stmt|;
block|}
return|return
name|feed
return|;
block|}
DECL|method|next
specifier|private
name|int
name|next
parameter_list|()
throws|throws
name|XMLStreamException
block|{
return|return
name|this
operator|.
name|streamReader
operator|.
name|next
argument_list|()
return|;
block|}
block|}
end_class
end_unit
