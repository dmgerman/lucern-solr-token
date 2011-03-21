begin_unit
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermToBytesRefAttribute
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
name|TermsFilter
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
name|util
operator|.
name|BytesRef
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  *   */
end_comment
begin_class
DECL|class|TermsFilterBuilder
specifier|public
class|class
name|TermsFilterBuilder
implements|implements
name|FilterBuilder
block|{
DECL|field|analyzer
name|Analyzer
name|analyzer
decl_stmt|;
comment|/** 	 * @param analyzer 	 */
DECL|method|TermsFilterBuilder
specifier|public
name|TermsFilterBuilder
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.apache.lucene.xmlparser.FilterBuilder#process(org.w3c.dom.Element) 	 */
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
name|TermsFilter
name|tf
init|=
operator|new
name|TermsFilter
argument_list|()
decl_stmt|;
name|String
name|text
init|=
name|DOMUtils
operator|.
name|getNonBlankTextOrFail
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|String
name|fieldName
init|=
name|DOMUtils
operator|.
name|getAttributeWithInheritanceOrFail
argument_list|(
name|e
argument_list|,
literal|"fieldName"
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|TermToBytesRefAttribute
name|termAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|Term
name|term
init|=
literal|null
decl_stmt|;
name|BytesRef
name|bytes
init|=
name|termAtt
operator|.
name|getBytesRef
argument_list|()
decl_stmt|;
while|while
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|termAtt
operator|.
name|fillBytesRef
argument_list|()
expr_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
name|term
operator|=
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//					 create from previous to save fieldName.intern overhead
name|term
operator|=
name|term
operator|.
name|createTerm
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|tf
operator|.
name|addTerm
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error constructing terms from index:"
operator|+
name|ioe
argument_list|)
throw|;
block|}
return|return
name|tf
return|;
block|}
block|}
end_class
end_unit
