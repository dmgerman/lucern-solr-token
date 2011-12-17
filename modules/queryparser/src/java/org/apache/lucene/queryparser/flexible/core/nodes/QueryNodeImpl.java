begin_unit
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.core.nodes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|nodes
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|ResourceBundle
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
name|queryparser
operator|.
name|flexible
operator|.
name|messages
operator|.
name|NLS
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|messages
operator|.
name|QueryParserMessages
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|util
operator|.
name|StringUtils
import|;
end_import
begin_comment
comment|/**  * A {@link QueryNodeImpl} is the default implementation of the interface  * {@link QueryNode}  */
end_comment
begin_class
DECL|class|QueryNodeImpl
specifier|public
specifier|abstract
class|class
name|QueryNodeImpl
implements|implements
name|QueryNode
implements|,
name|Cloneable
block|{
comment|/* index default field */
comment|// TODO remove PLAINTEXT_FIELD_NAME replacing it with configuration APIs
DECL|field|PLAINTEXT_FIELD_NAME
specifier|public
specifier|static
specifier|final
name|String
name|PLAINTEXT_FIELD_NAME
init|=
literal|"_plain"
decl_stmt|;
DECL|field|isLeaf
specifier|private
name|boolean
name|isLeaf
init|=
literal|true
decl_stmt|;
DECL|field|tags
specifier|private
name|Hashtable
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|tags
init|=
operator|new
name|Hashtable
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|clauses
specifier|private
name|List
argument_list|<
name|QueryNode
argument_list|>
name|clauses
init|=
literal|null
decl_stmt|;
DECL|method|allocate
specifier|protected
name|void
name|allocate
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|clauses
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|clauses
operator|=
operator|new
name|ArrayList
argument_list|<
name|QueryNode
argument_list|>
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|clauses
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|add
specifier|public
specifier|final
name|void
name|add
parameter_list|(
name|QueryNode
name|child
parameter_list|)
block|{
if|if
condition|(
name|isLeaf
argument_list|()
operator|||
name|this
operator|.
name|clauses
operator|==
literal|null
operator|||
name|child
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|NLS
operator|.
name|getLocalizedMessage
argument_list|(
name|QueryParserMessages
operator|.
name|NODE_ACTION_NOT_SUPPORTED
argument_list|)
argument_list|)
throw|;
block|}
name|this
operator|.
name|clauses
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
operator|(
operator|(
name|QueryNodeImpl
operator|)
name|child
operator|)
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|add
specifier|public
specifier|final
name|void
name|add
parameter_list|(
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
parameter_list|)
block|{
if|if
condition|(
name|isLeaf
argument_list|()
operator|||
name|this
operator|.
name|clauses
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|NLS
operator|.
name|getLocalizedMessage
argument_list|(
name|QueryParserMessages
operator|.
name|NODE_ACTION_NOT_SUPPORTED
argument_list|)
argument_list|)
throw|;
block|}
for|for
control|(
name|QueryNode
name|child
range|:
name|children
control|)
block|{
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|isLeaf
specifier|public
name|boolean
name|isLeaf
parameter_list|()
block|{
return|return
name|this
operator|.
name|isLeaf
return|;
block|}
DECL|method|set
specifier|public
specifier|final
name|void
name|set
parameter_list|(
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
parameter_list|)
block|{
if|if
condition|(
name|isLeaf
argument_list|()
operator|||
name|this
operator|.
name|clauses
operator|==
literal|null
condition|)
block|{
name|ResourceBundle
name|bundle
init|=
name|ResourceBundle
operator|.
name|getBundle
argument_list|(
literal|"org.apache.lucene.queryParser.messages.QueryParserMessages"
argument_list|)
decl_stmt|;
name|String
name|message
init|=
name|bundle
operator|.
name|getObject
argument_list|(
literal|"Q0008E.NODE_ACTION_NOT_SUPPORTED"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|message
argument_list|)
throw|;
block|}
comment|// reset parent value
for|for
control|(
name|QueryNode
name|child
range|:
name|children
control|)
block|{
operator|(
operator|(
name|QueryNodeImpl
operator|)
name|child
operator|)
operator|.
name|setParent
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// allocate new children list
name|allocate
argument_list|()
expr_stmt|;
comment|// add new children and set parent
for|for
control|(
name|QueryNode
name|child
range|:
name|children
control|)
block|{
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|cloneTree
specifier|public
name|QueryNode
name|cloneTree
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
name|QueryNodeImpl
name|clone
init|=
operator|(
name|QueryNodeImpl
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|isLeaf
operator|=
name|this
operator|.
name|isLeaf
expr_stmt|;
comment|// Reset all tags
name|clone
operator|.
name|tags
operator|=
operator|new
name|Hashtable
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
comment|// copy children
if|if
condition|(
name|this
operator|.
name|clauses
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|QueryNode
argument_list|>
name|localClauses
init|=
operator|new
name|ArrayList
argument_list|<
name|QueryNode
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|QueryNode
name|clause
range|:
name|this
operator|.
name|clauses
control|)
block|{
name|localClauses
operator|.
name|add
argument_list|(
name|clause
operator|.
name|cloneTree
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|clone
operator|.
name|clauses
operator|=
name|localClauses
expr_stmt|;
block|}
return|return
name|clone
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
return|return
name|cloneTree
argument_list|()
return|;
block|}
DECL|method|setLeaf
specifier|protected
name|void
name|setLeaf
parameter_list|(
name|boolean
name|isLeaf
parameter_list|)
block|{
name|this
operator|.
name|isLeaf
operator|=
name|isLeaf
expr_stmt|;
block|}
comment|/**    * @return a List for QueryNode object. Returns null, for nodes that do not    *         contain children. All leaf Nodes return null.    */
DECL|method|getChildren
specifier|public
specifier|final
name|List
argument_list|<
name|QueryNode
argument_list|>
name|getChildren
parameter_list|()
block|{
if|if
condition|(
name|isLeaf
argument_list|()
operator|||
name|this
operator|.
name|clauses
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|this
operator|.
name|clauses
return|;
block|}
DECL|method|setTag
specifier|public
name|void
name|setTag
parameter_list|(
name|String
name|tagName
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|tags
operator|.
name|put
argument_list|(
name|tagName
operator|.
name|toLowerCase
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|unsetTag
specifier|public
name|void
name|unsetTag
parameter_list|(
name|String
name|tagName
parameter_list|)
block|{
name|this
operator|.
name|tags
operator|.
name|remove
argument_list|(
name|tagName
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** verify if a node contains a tag */
DECL|method|containsTag
specifier|public
name|boolean
name|containsTag
parameter_list|(
name|String
name|tagName
parameter_list|)
block|{
return|return
name|this
operator|.
name|tags
operator|.
name|containsKey
argument_list|(
name|tagName
operator|.
name|toLowerCase
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getTag
specifier|public
name|Object
name|getTag
parameter_list|(
name|String
name|tagName
parameter_list|)
block|{
return|return
name|this
operator|.
name|tags
operator|.
name|get
argument_list|(
name|tagName
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
return|;
block|}
DECL|field|parent
specifier|private
name|QueryNode
name|parent
init|=
literal|null
decl_stmt|;
DECL|method|setParent
specifier|private
name|void
name|setParent
parameter_list|(
name|QueryNode
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
DECL|method|getParent
specifier|public
name|QueryNode
name|getParent
parameter_list|()
block|{
return|return
name|this
operator|.
name|parent
return|;
block|}
DECL|method|isRoot
specifier|protected
name|boolean
name|isRoot
parameter_list|()
block|{
return|return
name|getParent
argument_list|()
operator|==
literal|null
return|;
block|}
comment|/**    * If set to true the the method toQueryString will not write field names    */
DECL|field|toQueryStringIgnoreFields
specifier|protected
name|boolean
name|toQueryStringIgnoreFields
init|=
literal|false
decl_stmt|;
comment|/**    * This method is use toQueryString to detect if fld is the default field    *     * @param fld - field name    * @return true if fld is the default field    */
comment|// TODO: remove this method, it's commonly used by {@link
comment|// #toQueryString(org.apache.lucene.queryParser.core.parser.EscapeQuerySyntax)}
comment|// to figure out what is the default field, however, {@link
comment|// #toQueryString(org.apache.lucene.queryParser.core.parser.EscapeQuerySyntax)}
comment|// should receive the default field value directly by parameter
DECL|method|isDefaultField
specifier|protected
name|boolean
name|isDefaultField
parameter_list|(
name|CharSequence
name|fld
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|toQueryStringIgnoreFields
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|fld
operator|==
literal|null
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|QueryNodeImpl
operator|.
name|PLAINTEXT_FIELD_NAME
operator|.
name|equals
argument_list|(
name|StringUtils
operator|.
name|toString
argument_list|(
name|fld
argument_list|)
argument_list|)
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
comment|/**    * Every implementation of this class should return pseudo xml like this:    *     * For FieldQueryNode:<field start='1' end='2' field='subject' text='foo'/>    *     * @see org.apache.lucene.queryparser.flexible.core.nodes.QueryNode#toString()    */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Returns a map containing all tags attached to this query node.    *     * @return a map containing all tags attached to this query node    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getTagMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getTagMap
parameter_list|()
block|{
return|return
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|this
operator|.
name|tags
operator|.
name|clone
argument_list|()
return|;
block|}
block|}
end_class
begin_comment
comment|// end class QueryNodeImpl
end_comment
end_unit
