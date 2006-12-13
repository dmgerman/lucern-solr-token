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
begin_comment
comment|/**  * @author Simon Willnauer  *   */
end_comment
begin_class
DECL|class|GOMAttributeImpl
specifier|public
class|class
name|GOMAttributeImpl
implements|implements
name|GOMAttribute
block|{
DECL|field|hasDefaultNamespace
specifier|private
name|boolean
name|hasDefaultNamespace
decl_stmt|;
DECL|field|qName
specifier|private
name|QName
name|qName
decl_stmt|;
DECL|field|localName
specifier|private
name|String
name|localName
decl_stmt|;
DECL|field|uri
specifier|private
name|String
name|uri
decl_stmt|;
DECL|field|prefix
specifier|private
name|String
name|prefix
decl_stmt|;
DECL|field|value
specifier|private
name|String
name|value
decl_stmt|;
comment|/** 	 *  	 */
DECL|method|GOMAttributeImpl
specifier|public
name|GOMAttributeImpl
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @param localName 	 * @param value 	 */
DECL|method|GOMAttributeImpl
specifier|public
name|GOMAttributeImpl
parameter_list|(
name|String
name|localName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|hasDefaultNamespace
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|localName
operator|=
name|localName
expr_stmt|;
block|}
comment|/** 	 * @param namespaceUri 	 * @param namespacePrefix 	 * @param localName 	 * @param value 	 */
DECL|method|GOMAttributeImpl
specifier|public
name|GOMAttributeImpl
parameter_list|(
name|String
name|namespaceUri
parameter_list|,
name|String
name|namespacePrefix
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|localName
operator|=
name|localName
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|namespaceUri
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|namespacePrefix
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMXmlEntity#getQname() 	 */
DECL|method|getQname
specifier|public
name|QName
name|getQname
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|qName
operator|==
literal|null
condition|)
name|this
operator|.
name|qName
operator|=
operator|new
name|QName
argument_list|(
name|this
operator|.
name|uri
argument_list|,
operator|(
name|this
operator|.
name|localName
operator|==
literal|null
condition|?
literal|""
else|:
name|this
operator|.
name|localName
operator|)
argument_list|,
operator|(
name|this
operator|.
name|prefix
operator|==
literal|null
condition|?
literal|""
else|:
name|this
operator|.
name|prefix
operator|)
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|qName
return|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMXmlEntity#setLocalName(java.lang.String) 	 */
DECL|method|setLocalName
specifier|public
name|void
name|setLocalName
parameter_list|(
name|String
name|aLocalName
parameter_list|)
block|{
if|if
condition|(
name|aLocalName
operator|==
literal|null
condition|)
return|return;
name|this
operator|.
name|qName
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|localName
operator|=
name|aLocalName
expr_stmt|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMXmlEntity#getLocalName() 	 */
DECL|method|getLocalName
specifier|public
name|String
name|getLocalName
parameter_list|()
block|{
return|return
name|this
operator|.
name|localName
return|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMXmlEntity#getTextValue() 	 */
DECL|method|getTextValue
specifier|public
name|String
name|getTextValue
parameter_list|()
block|{
return|return
name|this
operator|.
name|value
operator|==
literal|null
condition|?
literal|""
else|:
name|this
operator|.
name|value
return|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMXmlEntity#setTextValue(java.lang.String) 	 */
DECL|method|setTextValue
specifier|public
name|void
name|setTextValue
parameter_list|(
name|String
name|aTextValue
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|aTextValue
expr_stmt|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMAttribute#hasDefaultNamespace() 	 */
DECL|method|hasDefaultNamespace
specifier|public
name|boolean
name|hasDefaultNamespace
parameter_list|()
block|{
return|return
name|this
operator|.
name|hasDefaultNamespace
return|;
block|}
DECL|method|setHasDefaultNamespace
name|void
name|setHasDefaultNamespace
parameter_list|(
name|boolean
name|aBoolean
parameter_list|)
block|{
name|this
operator|.
name|hasDefaultNamespace
operator|=
name|aBoolean
expr_stmt|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMXmlEntity#setNamespaceUri(java.lang.String) 	 */
DECL|method|setNamespaceUri
specifier|public
name|void
name|setNamespaceUri
parameter_list|(
name|String
name|aString
parameter_list|)
block|{
if|if
condition|(
name|aString
operator|==
literal|null
condition|)
return|return;
name|this
operator|.
name|qName
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|hasDefaultNamespace
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|aString
expr_stmt|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMXmlEntity#setNamespacePrefix(java.lang.String) 	 */
DECL|method|setNamespacePrefix
specifier|public
name|void
name|setNamespacePrefix
parameter_list|(
name|String
name|aString
parameter_list|)
block|{
if|if
condition|(
name|aString
operator|==
literal|null
condition|)
return|return;
name|this
operator|.
name|qName
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|hasDefaultNamespace
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|aString
expr_stmt|;
block|}
block|}
end_class
end_unit
