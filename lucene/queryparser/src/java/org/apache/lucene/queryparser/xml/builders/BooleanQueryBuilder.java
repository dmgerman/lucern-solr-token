begin_unit
begin_comment
comment|/*  * Created on 25-Jan-2006  */
end_comment
begin_package
DECL|package|org.apache.lucene.queryparser.xml.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|xml
operator|.
name|builders
package|;
end_package
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
name|BooleanClause
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
name|BooleanQuery
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
name|Query
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
name|xml
operator|.
name|DOMUtils
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
name|xml
operator|.
name|ParserException
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
name|xml
operator|.
name|QueryBuilder
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Builder for {@link BooleanQuery}  */
end_comment
begin_class
DECL|class|BooleanQueryBuilder
specifier|public
class|class
name|BooleanQueryBuilder
implements|implements
name|QueryBuilder
block|{
DECL|field|factory
specifier|private
specifier|final
name|QueryBuilder
name|factory
decl_stmt|;
DECL|method|BooleanQueryBuilder
specifier|public
name|BooleanQueryBuilder
parameter_list|(
name|QueryBuilder
name|factory
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
comment|/* (non-Javadoc)     * @see org.apache.lucene.xmlparser.QueryObjectBuilder#process(org.w3c.dom.Element)     */
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
block|{
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|(
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"disableCoord"
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|bq
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"minimumNumberShouldMatch"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|bq
operator|.
name|setBoost
argument_list|(
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"boost"
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
name|NodeList
name|nl
init|=
name|e
operator|.
name|getChildNodes
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
name|nl
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|node
init|=
name|nl
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Clause"
argument_list|)
condition|)
block|{
name|Element
name|clauseElem
init|=
operator|(
name|Element
operator|)
name|node
decl_stmt|;
name|BooleanClause
operator|.
name|Occur
name|occurs
init|=
name|getOccursValue
argument_list|(
name|clauseElem
argument_list|)
decl_stmt|;
name|Element
name|clauseQuery
init|=
name|DOMUtils
operator|.
name|getFirstChildOrFail
argument_list|(
name|clauseElem
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
name|factory
operator|.
name|getQuery
argument_list|(
name|clauseQuery
argument_list|)
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|q
argument_list|,
name|occurs
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|bq
return|;
block|}
DECL|method|getOccursValue
specifier|static
name|BooleanClause
operator|.
name|Occur
name|getOccursValue
parameter_list|(
name|Element
name|clauseElem
parameter_list|)
throws|throws
name|ParserException
block|{
name|String
name|occs
init|=
name|clauseElem
operator|.
name|getAttribute
argument_list|(
literal|"occurs"
argument_list|)
decl_stmt|;
name|BooleanClause
operator|.
name|Occur
name|occurs
init|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
decl_stmt|;
if|if
condition|(
literal|"must"
operator|.
name|equalsIgnoreCase
argument_list|(
name|occs
argument_list|)
condition|)
block|{
name|occurs
operator|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
literal|"mustNot"
operator|.
name|equalsIgnoreCase
argument_list|(
name|occs
argument_list|)
condition|)
block|{
name|occurs
operator|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|(
literal|"should"
operator|.
name|equalsIgnoreCase
argument_list|(
name|occs
argument_list|)
operator|)
operator|||
operator|(
literal|""
operator|.
name|equals
argument_list|(
name|occs
argument_list|)
operator|)
condition|)
block|{
name|occurs
operator|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|occs
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
literal|"Invalid value for \"occurs\" attribute of clause:"
operator|+
name|occs
argument_list|)
throw|;
block|}
block|}
block|}
block|}
return|return
name|occurs
return|;
block|}
block|}
end_class
end_unit
