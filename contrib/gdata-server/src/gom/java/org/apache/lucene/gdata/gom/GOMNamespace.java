begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.gom
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
package|;
end_package
begin_comment
comment|/**  * A simple domain object to represent a xml namespace.  *   * @author Simon Willnauer  *   */
end_comment
begin_class
DECL|class|GOMNamespace
specifier|public
specifier|final
class|class
name|GOMNamespace
block|{
comment|/** 	 * XML namespace uri 	 */
DECL|field|XML_NS_URI
specifier|public
specifier|static
specifier|final
name|String
name|XML_NS_URI
init|=
literal|"http://www.w3.org/XML/1998/namespace"
decl_stmt|;
comment|/** 	 * XML namespace prefix 	 */
DECL|field|XML_NS_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|XML_NS_PREFIX
init|=
literal|"xml"
decl_stmt|;
comment|/** 	 * Amazon "opensearch" namespace prefix 	 */
DECL|field|OPENSEARCH_NS_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|OPENSEARCH_NS_PREFIX
init|=
literal|"openSearch"
decl_stmt|;
comment|/** 	 * Amazon "opensearch" namespace uri 	 */
DECL|field|OPENSEARCH_NS_URI
specifier|public
specifier|static
specifier|final
name|String
name|OPENSEARCH_NS_URI
init|=
literal|"http://a9.com/-/spec/opensearchrss/1.0/"
decl_stmt|;
comment|/** 	 * ATOM namespace uri 	 */
DECL|field|ATOM_NS_URI
specifier|public
specifier|static
specifier|final
name|String
name|ATOM_NS_URI
init|=
literal|"http://www.w3.org/2005/Atom"
decl_stmt|;
comment|/** 	 * ATOM namespace prefix 	 */
DECL|field|ATOM_NS_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|ATOM_NS_PREFIX
init|=
literal|"atom"
decl_stmt|;
comment|/** 	 * ATOM namespace 	 */
DECL|field|ATOM_NAMESPACE
specifier|public
specifier|static
specifier|final
name|GOMNamespace
name|ATOM_NAMESPACE
init|=
operator|new
name|GOMNamespace
argument_list|(
name|ATOM_NS_URI
argument_list|,
name|ATOM_NS_PREFIX
argument_list|)
decl_stmt|;
comment|/** 	 * Amazon "opensearch" namespace 	 */
DECL|field|OPENSEARCH_NAMESPACE
specifier|public
specifier|static
specifier|final
name|GOMNamespace
name|OPENSEARCH_NAMESPACE
init|=
operator|new
name|GOMNamespace
argument_list|(
name|OPENSEARCH_NS_URI
argument_list|,
name|OPENSEARCH_NS_PREFIX
argument_list|)
decl_stmt|;
DECL|field|namespaceUri
specifier|private
specifier|final
name|String
name|namespaceUri
decl_stmt|;
DECL|field|namespacePrefix
specifier|private
specifier|final
name|String
name|namespacePrefix
decl_stmt|;
comment|/** 	 * Class constructor for GOMNamespace 	 *  	 * @param aNamespaceUri - 	 *            the namespace uri (must not be null) 	 * @param aNamespacePrefix - 	 *            the namespace prefix (if null an empty string will be 	 *            assigned) 	 *  	 */
DECL|method|GOMNamespace
specifier|public
name|GOMNamespace
parameter_list|(
specifier|final
name|String
name|aNamespaceUri
parameter_list|,
specifier|final
name|String
name|aNamespacePrefix
parameter_list|)
block|{
if|if
condition|(
name|aNamespaceUri
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"uri must not be null"
argument_list|)
throw|;
name|this
operator|.
name|namespacePrefix
operator|=
name|aNamespacePrefix
operator|==
literal|null
condition|?
literal|""
else|:
name|aNamespacePrefix
expr_stmt|;
name|this
operator|.
name|namespaceUri
operator|=
name|aNamespaceUri
expr_stmt|;
block|}
comment|/** 	 * @return Returns the namespacePrefix. 	 */
DECL|method|getNamespacePrefix
specifier|public
name|String
name|getNamespacePrefix
parameter_list|()
block|{
return|return
name|this
operator|.
name|namespacePrefix
return|;
block|}
comment|/** 	 * @return Returns the namespaceUri. 	 */
DECL|method|getNamespaceUri
specifier|public
name|String
name|getNamespaceUri
parameter_list|()
block|{
return|return
name|this
operator|.
name|namespaceUri
return|;
block|}
comment|/** 	 * @see java.lang.Object#equals(java.lang.Object) 	 */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|arg0
parameter_list|)
block|{
if|if
condition|(
name|arg0
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|arg0
operator|==
name|this
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|arg0
operator|instanceof
name|GOMNamespace
condition|)
block|{
name|GOMNamespace
name|other
init|=
operator|(
name|GOMNamespace
operator|)
name|arg0
decl_stmt|;
return|return
name|this
operator|.
name|namespacePrefix
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getNamespacePrefix
argument_list|()
argument_list|)
operator|&&
name|this
operator|.
name|namespaceUri
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getNamespaceUri
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/** 	 * @see java.lang.Object#hashCode() 	 */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|/* 		 * The multiplier 37 was chosen because it is an odd prime. If it was 		 * even and the multiplication overflowed, information would be lost 		 * because multiplication by two is equivalent to shifting The value 17 		 * is arbitrary. see 		 * http://java.sun.com/developer/Books/effectivejava/Chapter3.pdf 		 */
name|int
name|hash
init|=
literal|17
decl_stmt|;
name|hash
operator|=
literal|37
operator|*
name|hash
operator|+
name|this
operator|.
name|namespacePrefix
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|hash
operator|=
literal|37
operator|*
name|hash
operator|+
name|this
operator|.
name|namespaceUri
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|hash
return|;
block|}
comment|/** 	 * @see java.lang.Object#toString() 	 */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|" uri: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|namespaceUri
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|" prefix: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|namespacePrefix
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
