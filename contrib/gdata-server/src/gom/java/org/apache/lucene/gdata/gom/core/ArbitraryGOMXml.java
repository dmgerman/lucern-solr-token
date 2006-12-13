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
name|LinkedList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
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
name|GOMAttribute
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
name|GOMElement
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
name|writer
operator|.
name|GOMOutputWriter
import|;
end_import
begin_comment
comment|//TODO add java doc
end_comment
begin_comment
comment|/**  *   * @author Simon Willnauer  *   */
end_comment
begin_class
DECL|class|ArbitraryGOMXml
specifier|public
class|class
name|ArbitraryGOMXml
extends|extends
name|AbstractGOMElement
block|{
DECL|field|children
specifier|private
name|List
argument_list|<
name|GOMElement
argument_list|>
name|children
init|=
operator|new
name|LinkedList
argument_list|<
name|GOMElement
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|attributes
specifier|private
name|List
argument_list|<
name|GOMAttribute
argument_list|>
name|attributes
init|=
operator|new
name|LinkedList
argument_list|<
name|GOMAttribute
argument_list|>
argument_list|()
decl_stmt|;
comment|/** 	 * this method will never return<code>null</code> 	 *  	 * @return Returns the attributes of this xml element. 	 */
DECL|method|getAttributes
specifier|public
name|List
argument_list|<
name|GOMAttribute
argument_list|>
name|getAttributes
parameter_list|()
block|{
return|return
name|this
operator|.
name|attributes
return|;
block|}
comment|/** 	 * this method will never return<code>null</code> 	 *  	 * @return - the child elements of this xml element 	 */
DECL|method|getChildren
specifier|public
name|List
argument_list|<
name|GOMElement
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|this
operator|.
name|children
return|;
block|}
comment|/** 	 * Class constructor 	 *  	 * @param qname - 	 *            the elements qname 	 */
DECL|method|ArbitraryGOMXml
specifier|public
name|ArbitraryGOMXml
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
if|if
condition|(
name|qname
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"QName must not be null"
argument_list|)
throw|;
name|this
operator|.
name|qname
operator|=
name|qname
expr_stmt|;
name|this
operator|.
name|localName
operator|=
name|qname
operator|.
name|getLocalPart
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * {@inheritDoc}  	 *  	 * @see org.apache.lucene.gdata.gom.core.AbstractGOMElement#getChildParser(javax.xml.namespace.QName) 	 */
annotation|@
name|Override
DECL|method|getChildParser
specifier|public
name|AtomParser
name|getChildParser
parameter_list|(
name|QName
name|aName
parameter_list|)
block|{
if|if
condition|(
name|aName
operator|==
literal|null
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
literal|"QName must not be null"
argument_list|)
throw|;
comment|/* 		 * either a text value or a child 		 */
if|if
condition|(
name|this
operator|.
name|textValue
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|AtomParser
operator|.
name|UNEXPECTED_ELEMENT_CHILD
argument_list|,
name|this
operator|.
name|localName
argument_list|)
argument_list|)
throw|;
name|GOMElement
name|element
init|=
operator|new
name|ArbitraryGOMXml
argument_list|(
name|aName
argument_list|)
decl_stmt|;
name|this
operator|.
name|children
operator|.
name|add
argument_list|(
name|element
argument_list|)
expr_stmt|;
return|return
name|element
return|;
block|}
comment|/** 	 * {@inheritDoc}  	 *  	 * @see org.apache.lucene.gdata.gom.core.AbstractGOMElement#processAttribute(javax.xml.namespace.QName, 	 *      java.lang.String) 	 */
annotation|@
name|Override
DECL|method|processAttribute
specifier|public
name|void
name|processAttribute
parameter_list|(
name|QName
name|aQName
parameter_list|,
name|String
name|aValue
parameter_list|)
block|{
if|if
condition|(
name|aQName
operator|==
literal|null
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
literal|"QName must not be null"
argument_list|)
throw|;
name|GOMAttributeImpl
name|impl
init|=
operator|new
name|GOMAttributeImpl
argument_list|(
name|aQName
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|aQName
operator|.
name|getPrefix
argument_list|()
argument_list|,
name|aQName
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|aValue
argument_list|)
decl_stmt|;
name|this
operator|.
name|attributes
operator|.
name|add
argument_list|(
name|impl
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * {@inheritDoc} 	 *  	 * @see org.apache.lucene.gdata.gom.core.AbstractGOMElement#processElementValue(java.lang.String) 	 */
annotation|@
name|Override
DECL|method|processElementValue
specifier|public
name|void
name|processElementValue
parameter_list|(
name|String
name|aValue
parameter_list|)
block|{
if|if
condition|(
name|aValue
operator|==
literal|null
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
literal|"Element value must not be null"
argument_list|)
throw|;
comment|/* 		 * either a text value or a child 		 */
if|if
condition|(
name|this
operator|.
name|children
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|AtomParser
operator|.
name|UNEXPECTED_ELEMENT_VALUE
argument_list|,
name|this
operator|.
name|localName
argument_list|)
argument_list|)
throw|;
if|if
condition|(
name|this
operator|.
name|textValue
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|AtomParser
operator|.
name|UNEXPECTED_ELEMENT_VALUE
argument_list|,
name|this
operator|.
name|localName
argument_list|)
argument_list|)
throw|;
name|this
operator|.
name|textValue
operator|=
name|aValue
expr_stmt|;
block|}
comment|/** 	 * {@inheritDoc} 	 *  	 * @see org.apache.lucene.gdata.gom.GOMElement#writeAtomOutput(org.apache.lucene.gdata.gom.writer.GOMStaxWriter) 	 */
DECL|method|writeAtomOutput
specifier|public
name|void
name|writeAtomOutput
parameter_list|(
name|GOMOutputWriter
name|aStreamWriter
parameter_list|)
throws|throws
name|XMLStreamException
block|{
if|if
condition|(
name|aStreamWriter
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"StreamWriter is null"
argument_list|)
throw|;
name|aStreamWriter
operator|.
name|writeStartElement
argument_list|(
name|this
operator|.
name|qname
argument_list|,
name|this
operator|.
name|attributes
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|textValue
operator|==
literal|null
condition|)
block|{
for|for
control|(
name|GOMElement
name|element
range|:
name|this
operator|.
name|children
control|)
block|{
name|element
operator|.
name|writeAtomOutput
argument_list|(
name|aStreamWriter
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|aStreamWriter
operator|.
name|writeContent
argument_list|(
name|this
operator|.
name|textValue
argument_list|)
expr_stmt|;
block|}
name|aStreamWriter
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * {@inheritDoc} 	 *  	 * @see org.apache.lucene.gdata.gom.GOMElement#writeRssOutput(org.apache.lucene.gdata.gom.writer.GOMStaxWriter) 	 */
DECL|method|writeRssOutput
specifier|public
name|void
name|writeRssOutput
parameter_list|(
name|GOMOutputWriter
name|aStreamWriter
parameter_list|)
throws|throws
name|XMLStreamException
block|{
comment|// delegate it by default
name|this
operator|.
name|writeAtomOutput
argument_list|(
name|aStreamWriter
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
