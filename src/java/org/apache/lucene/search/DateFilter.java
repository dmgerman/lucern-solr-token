begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|DateField
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
name|TermDocs
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
name|TermEnum
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
begin_comment
comment|/** A Filter that restricts search results to a range of time.<p>For this to work, documents must have been indexed with a {@link    DateField}.  */
end_comment
begin_class
DECL|class|DateFilter
specifier|public
class|class
name|DateFilter
extends|extends
name|Filter
block|{
DECL|field|field
name|String
name|field
decl_stmt|;
DECL|field|start
name|String
name|start
init|=
name|DateField
operator|.
name|MIN_DATE_STRING
argument_list|()
decl_stmt|;
DECL|field|end
name|String
name|end
init|=
name|DateField
operator|.
name|MAX_DATE_STRING
argument_list|()
decl_stmt|;
DECL|method|DateFilter
specifier|private
name|DateFilter
parameter_list|(
name|String
name|f
parameter_list|)
block|{
name|field
operator|=
name|f
expr_stmt|;
block|}
comment|/** Constructs a filter for field<code>f</code> matching dates between<code>from</code> and<code>to</code>. */
DECL|method|DateFilter
specifier|public
name|DateFilter
parameter_list|(
name|String
name|f
parameter_list|,
name|Date
name|from
parameter_list|,
name|Date
name|to
parameter_list|)
block|{
name|field
operator|=
name|f
expr_stmt|;
name|start
operator|=
name|DateField
operator|.
name|dateToString
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|end
operator|=
name|DateField
operator|.
name|dateToString
argument_list|(
name|to
argument_list|)
expr_stmt|;
block|}
comment|/** Constructs a filter for field<code>f</code> matching times between<code>from</code> and<code>to</code>. */
DECL|method|DateFilter
specifier|public
name|DateFilter
parameter_list|(
name|String
name|f
parameter_list|,
name|long
name|from
parameter_list|,
name|long
name|to
parameter_list|)
block|{
name|field
operator|=
name|f
expr_stmt|;
name|start
operator|=
name|DateField
operator|.
name|timeToString
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|end
operator|=
name|DateField
operator|.
name|timeToString
argument_list|(
name|to
argument_list|)
expr_stmt|;
block|}
comment|/** Constructs a filter for field<code>f</code> matching dates before<code>date</code>. */
DECL|method|Before
specifier|public
specifier|static
name|DateFilter
name|Before
parameter_list|(
name|String
name|field
parameter_list|,
name|Date
name|date
parameter_list|)
block|{
name|DateFilter
name|result
init|=
operator|new
name|DateFilter
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|result
operator|.
name|end
operator|=
name|DateField
operator|.
name|dateToString
argument_list|(
name|date
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** Constructs a filter for field<code>f</code> matching times before<code>time</code>. */
DECL|method|Before
specifier|public
specifier|static
name|DateFilter
name|Before
parameter_list|(
name|String
name|field
parameter_list|,
name|long
name|time
parameter_list|)
block|{
name|DateFilter
name|result
init|=
operator|new
name|DateFilter
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|result
operator|.
name|end
operator|=
name|DateField
operator|.
name|timeToString
argument_list|(
name|time
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** Constructs a filter for field<code>f</code> matching dates after<code>date</code>. */
DECL|method|After
specifier|public
specifier|static
name|DateFilter
name|After
parameter_list|(
name|String
name|field
parameter_list|,
name|Date
name|date
parameter_list|)
block|{
name|DateFilter
name|result
init|=
operator|new
name|DateFilter
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|result
operator|.
name|start
operator|=
name|DateField
operator|.
name|dateToString
argument_list|(
name|date
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** Constructs a filter for field<code>f</code> matching times after<code>time</code>. */
DECL|method|After
specifier|public
specifier|static
name|DateFilter
name|After
parameter_list|(
name|String
name|field
parameter_list|,
name|long
name|time
parameter_list|)
block|{
name|DateFilter
name|result
init|=
operator|new
name|DateFilter
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|result
operator|.
name|start
operator|=
name|DateField
operator|.
name|timeToString
argument_list|(
name|time
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** Returns a BitSet with true for documents which should be permitted in     search results, and false for those that should not. */
DECL|method|bits
specifier|public
name|BitSet
name|bits
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|BitSet
name|bits
init|=
operator|new
name|BitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|TermEnum
name|enum
type|=
name|reader
operator|.
name|terms
decl|(new
name|Term
decl|(
name|field
decl|,
name|start
decl_stmt|)
block|)
function|;
name|TermDocs
name|termDocs
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
if|if
condition|(enum
operator|.
name|term
argument_list|()
operator|==
literal|null
condition|)
return|return
name|bits
return|;
try|try
block|{
name|Term
name|stop
init|=
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|end
argument_list|)
decl_stmt|;
while|while
condition|(enum
operator|.
name|term
argument_list|()
operator|.
name|compareTo
argument_list|(
name|stop
argument_list|)
operator|<=
literal|0
condition|)
block|{
name|termDocs
operator|.
name|seek
argument_list|(
expr|enum
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
name|bits
operator|.
name|set
argument_list|(
name|termDocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
expr|enum
operator|.
name|next
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
block|}
finally|finally
block|{
enum_decl|enum.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|bits
return|;
block|}
end_class
begin_function
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|DateField
operator|.
name|stringToDate
argument_list|(
name|start
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"-"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|DateField
operator|.
name|stringToDate
argument_list|(
name|end
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
end_function
unit|}
end_unit
