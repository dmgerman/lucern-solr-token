begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|core
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|messages
operator|.
name|Message
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
name|messages
operator|.
name|MessageImpl
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
name|queryParser
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|QueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|parser
operator|.
name|SyntaxParser
import|;
end_import
begin_comment
comment|/**  * This should be thrown when an exception happens during the query parsing from  * string to the query node tree.  *   * @see QueryNodeException  * @see SyntaxParser  * @see QueryNode  */
end_comment
begin_class
DECL|class|QueryNodeParseException
specifier|public
class|class
name|QueryNodeParseException
extends|extends
name|QueryNodeException
block|{
DECL|field|query
specifier|private
name|CharSequence
name|query
decl_stmt|;
DECL|field|beginColumn
specifier|private
name|int
name|beginColumn
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|beginLine
specifier|private
name|int
name|beginLine
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|errorToken
specifier|private
name|String
name|errorToken
init|=
literal|""
decl_stmt|;
DECL|method|QueryNodeParseException
specifier|public
name|QueryNodeParseException
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|QueryNodeParseException
specifier|public
name|QueryNodeParseException
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
name|super
argument_list|(
name|throwable
argument_list|)
expr_stmt|;
block|}
DECL|method|QueryNodeParseException
specifier|public
name|QueryNodeParseException
parameter_list|(
name|Message
name|message
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
block|}
DECL|method|setQuery
specifier|public
name|void
name|setQuery
parameter_list|(
name|CharSequence
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|message
operator|=
operator|new
name|MessageImpl
argument_list|(
name|QueryParserMessages
operator|.
name|INVALID_SYNTAX_CANNOT_PARSE
argument_list|,
name|query
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|getQuery
specifier|public
name|CharSequence
name|getQuery
parameter_list|()
block|{
return|return
name|this
operator|.
name|query
return|;
block|}
comment|/**    * @param errorToken    *          the errorToken in the query    */
DECL|method|setErrorToken
specifier|protected
name|void
name|setErrorToken
parameter_list|(
name|String
name|errorToken
parameter_list|)
block|{
name|this
operator|.
name|errorToken
operator|=
name|errorToken
expr_stmt|;
block|}
DECL|method|getErrorToken
specifier|public
name|String
name|getErrorToken
parameter_list|()
block|{
return|return
name|this
operator|.
name|errorToken
return|;
block|}
DECL|method|setNonLocalizedMessage
specifier|public
name|void
name|setNonLocalizedMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
comment|/**    * For EndOfLine and EndOfFile ("<EOF>") parsing problems the last char in the    * string is returned For the case where the parser is not able to figure out    * the line and column number -1 will be returned    *     * @return line where the problem was found    */
DECL|method|getBeginLine
specifier|public
name|int
name|getBeginLine
parameter_list|()
block|{
return|return
name|this
operator|.
name|beginLine
return|;
block|}
comment|/**    * For EndOfLine and EndOfFile ("<EOF>") parsing problems the last char in the    * string is returned For the case where the parser is not able to figure out    * the line and column number -1 will be returned    *     * @return column of the first char where the problem was found    */
DECL|method|getBeginColumn
specifier|public
name|int
name|getBeginColumn
parameter_list|()
block|{
return|return
name|this
operator|.
name|beginColumn
return|;
block|}
comment|/**    * @param beginLine    *          the beginLine to set    */
DECL|method|setBeginLine
specifier|protected
name|void
name|setBeginLine
parameter_list|(
name|int
name|beginLine
parameter_list|)
block|{
name|this
operator|.
name|beginLine
operator|=
name|beginLine
expr_stmt|;
block|}
comment|/**    * @param beginColumn    *          the beginColumn to set    */
DECL|method|setBeginColumn
specifier|protected
name|void
name|setBeginColumn
parameter_list|(
name|int
name|beginColumn
parameter_list|)
block|{
name|this
operator|.
name|beginColumn
operator|=
name|beginColumn
expr_stmt|;
block|}
block|}
end_class
end_unit
