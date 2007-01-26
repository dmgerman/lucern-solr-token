begin_unit
begin_comment
comment|/*  * Created on 25-Jan-2006  */
end_comment
begin_package
DECL|package|org.apache.lucene.xmlparser.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|xmlparser
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
name|BooleanFilter
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
name|Filter
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
name|FilterClause
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
name|xmlparser
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
name|xmlparser
operator|.
name|FilterBuilder
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
name|xmlparser
operator|.
name|ParserException
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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * @author maharwood   */
end_comment
begin_class
DECL|class|BooleanFilterBuilder
specifier|public
class|class
name|BooleanFilterBuilder
implements|implements
name|FilterBuilder
block|{
DECL|field|factory
specifier|private
name|FilterBuilder
name|factory
decl_stmt|;
DECL|method|BooleanFilterBuilder
specifier|public
name|BooleanFilterBuilder
parameter_list|(
name|FilterBuilder
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
DECL|method|getFilter
specifier|public
name|Filter
name|getFilter
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
block|{
name|BooleanFilter
name|bf
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
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
name|BooleanQueryBuilder
operator|.
name|getOccursValue
argument_list|(
name|clauseElem
argument_list|)
decl_stmt|;
name|Element
name|clauseFilter
init|=
name|DOMUtils
operator|.
name|getFirstChildOrFail
argument_list|(
name|clauseElem
argument_list|)
decl_stmt|;
name|Filter
name|f
init|=
name|factory
operator|.
name|getFilter
argument_list|(
name|clauseFilter
argument_list|)
decl_stmt|;
name|bf
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|f
argument_list|,
name|occurs
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|bf
return|;
block|}
block|}
end_class
end_unit
