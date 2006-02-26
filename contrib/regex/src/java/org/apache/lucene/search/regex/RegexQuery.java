begin_unit
begin_package
DECL|package|org.apache.lucene.search.regex
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|regex
package|;
end_package
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|MultiTermQuery
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
name|FilteredTermEnum
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
name|index
operator|.
name|IndexReader
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
begin_comment
comment|/** Implements the regular expression term search query.  * The expressions supported depend on the regular expression implementation  * used by way of the {@link RegexCapabilities} interface.  *  * @see RegexTermEnum  */
end_comment
begin_class
DECL|class|RegexQuery
specifier|public
class|class
name|RegexQuery
extends|extends
name|MultiTermQuery
implements|implements
name|RegexQueryCapable
block|{
DECL|field|regexImpl
specifier|private
name|RegexCapabilities
name|regexImpl
init|=
operator|new
name|JavaUtilRegexCapabilities
argument_list|()
decl_stmt|;
comment|/** Constructs a query for terms matching<code>term</code>. */
DECL|method|RegexQuery
specifier|public
name|RegexQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|super
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
comment|/**    * Defines which {@link RegexCapabilities} implementation is used by this instance.    *    * @param impl    */
DECL|method|setRegexImplementation
specifier|public
name|void
name|setRegexImplementation
parameter_list|(
name|RegexCapabilities
name|impl
parameter_list|)
block|{
name|this
operator|.
name|regexImpl
operator|=
name|impl
expr_stmt|;
block|}
comment|/**    * @return The implementation used by this instance.    */
DECL|method|getRegexImplementation
specifier|public
name|RegexCapabilities
name|getRegexImplementation
parameter_list|()
block|{
return|return
name|regexImpl
return|;
block|}
DECL|method|getEnum
specifier|protected
name|FilteredTermEnum
name|getEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
argument_list|,
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|RegexTermEnum
argument_list|(
name|reader
argument_list|,
name|term
argument_list|,
name|regexImpl
argument_list|)
return|;
block|}
comment|/* generated by IntelliJ IDEA */
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
specifier|final
name|RegexQuery
name|that
init|=
operator|(
name|RegexQuery
operator|)
name|o
decl_stmt|;
return|return
name|regexImpl
operator|.
name|equals
argument_list|(
name|that
operator|.
name|regexImpl
argument_list|)
return|;
block|}
comment|/* generated by IntelliJ IDEA */
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|29
operator|*
name|result
operator|+
name|regexImpl
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
