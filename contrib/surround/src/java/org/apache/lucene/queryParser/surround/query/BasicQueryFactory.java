begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|surround
operator|.
name|query
package|;
end_package
begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/* Create basic queries to be used during rewrite.  * The basic queries are TermQuery and SpanTermQuery.  * An exception can be thrown when too many of these are used.  * SpanTermQuery and TermQuery use IndexReader.termEnum(Term), which causes the buffer usage.  *  * Use this class to limit the buffer usage for reading terms from an index.  * Default is 1024, the same as the max. number of subqueries for a BooleanQuery.  */
end_comment
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
name|Term
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
name|TermQuery
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
name|spans
operator|.
name|SpanTermQuery
import|;
end_import
begin_class
DECL|class|BasicQueryFactory
specifier|public
class|class
name|BasicQueryFactory
block|{
DECL|method|BasicQueryFactory
specifier|public
name|BasicQueryFactory
parameter_list|(
name|int
name|maxBasicQueries
parameter_list|)
block|{
name|this
operator|.
name|maxBasicQueries
operator|=
name|maxBasicQueries
expr_stmt|;
name|this
operator|.
name|queriesMade
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|BasicQueryFactory
specifier|public
name|BasicQueryFactory
parameter_list|()
block|{
name|this
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
block|}
DECL|field|maxBasicQueries
specifier|private
name|int
name|maxBasicQueries
decl_stmt|;
DECL|field|queriesMade
specifier|private
name|int
name|queriesMade
decl_stmt|;
DECL|method|getNrQueriesMade
specifier|public
name|int
name|getNrQueriesMade
parameter_list|()
block|{
return|return
name|queriesMade
return|;
block|}
DECL|method|getMaxBasicQueries
specifier|public
name|int
name|getMaxBasicQueries
parameter_list|()
block|{
return|return
name|maxBasicQueries
return|;
block|}
DECL|method|checkMax
specifier|private
specifier|synchronized
name|void
name|checkMax
parameter_list|()
throws|throws
name|TooManyBasicQueries
block|{
if|if
condition|(
name|queriesMade
operator|>=
name|maxBasicQueries
condition|)
throw|throw
operator|new
name|TooManyBasicQueries
argument_list|(
name|getMaxBasicQueries
argument_list|()
argument_list|)
throw|;
name|queriesMade
operator|++
expr_stmt|;
block|}
DECL|method|newTermQuery
specifier|public
name|TermQuery
name|newTermQuery
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|TooManyBasicQueries
block|{
name|checkMax
argument_list|()
expr_stmt|;
return|return
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
return|;
block|}
DECL|method|newSpanTermQuery
specifier|public
name|SpanTermQuery
name|newSpanTermQuery
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|TooManyBasicQueries
block|{
name|checkMax
argument_list|()
expr_stmt|;
return|return
operator|new
name|SpanTermQuery
argument_list|(
name|term
argument_list|)
return|;
block|}
block|}
end_class
end_unit
