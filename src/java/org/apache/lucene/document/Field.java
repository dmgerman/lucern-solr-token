begin_unit
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
begin_comment
comment|/**   A field is a section of a Document.  Each field has two parts, a name and a   value.  Values may be free text, provided as a String or as a Reader, or they   may be atomic keywords, which are not further processed.  Such keywords may   be used to represent dates, urls, etc.  Fields are optionally stored in the   index, so that they may be returned with hits on the document.   */
end_comment
begin_class
DECL|class|Field
specifier|public
specifier|final
class|class
name|Field
block|{
DECL|field|name
specifier|private
name|String
name|name
init|=
literal|"body"
decl_stmt|;
DECL|field|stringValue
specifier|private
name|String
name|stringValue
init|=
literal|null
decl_stmt|;
DECL|field|readerValue
specifier|private
name|Reader
name|readerValue
init|=
literal|null
decl_stmt|;
DECL|field|isStored
specifier|private
name|boolean
name|isStored
init|=
literal|false
decl_stmt|;
DECL|field|isIndexed
specifier|private
name|boolean
name|isIndexed
init|=
literal|true
decl_stmt|;
DECL|field|isTokenized
specifier|private
name|boolean
name|isTokenized
init|=
literal|true
decl_stmt|;
comment|/** Constructs a String-valued Field that is not tokenized, but is indexed     and stored.  Useful for non-text fields, e.g. date or url.  */
DECL|method|Keyword
specifier|public
specifier|static
specifier|final
name|Field
name|Keyword
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|Field
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Constructs a String-valued Field that is not tokenized nor indexed,     but is stored in the index, for return with hits. */
DECL|method|UnIndexed
specifier|public
specifier|static
specifier|final
name|Field
name|UnIndexed
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|Field
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Constructs a String-valued Field that is tokenized and indexed,     and is stored in the index, for return with hits.  Useful for short text     fields, like "title" or "subject". */
DECL|method|Text
specifier|public
specifier|static
specifier|final
name|Field
name|Text
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|Field
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/** Constructs a Date-valued Field that is tokenized and indexed,       and is stored in the index, for return with hits. */
DECL|method|Keyword
specifier|public
specifier|static
specifier|final
name|Field
name|Keyword
parameter_list|(
name|String
name|name
parameter_list|,
name|Date
name|value
parameter_list|)
block|{
return|return
operator|new
name|Field
argument_list|(
name|name
argument_list|,
name|DateField
operator|.
name|dateToString
argument_list|(
name|value
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/** Constructs a String-valued Field that is tokenized and indexed,     but that is not stored in the index. */
DECL|method|UnStored
specifier|public
specifier|static
specifier|final
name|Field
name|UnStored
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|Field
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/** Constructs a Reader-valued Field that is tokenized and indexed, but is     not stored in the index verbatim.  Useful for longer text fields, like     "body". */
DECL|method|Text
specifier|public
specifier|static
specifier|final
name|Field
name|Text
parameter_list|(
name|String
name|name
parameter_list|,
name|Reader
name|value
parameter_list|)
block|{
return|return
operator|new
name|Field
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/** The name of the field (e.g., "date", "subject", "title", "body", etc.)     as an interned string. */
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/** The value of the field as a String, or null.  If null, the Reader value     is used.  Exactly one of stringValue() and readerValue() must be set. */
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|()
block|{
return|return
name|stringValue
return|;
block|}
comment|/** The value of the field as a Reader, or null.  If null, the String value     is used.  Exactly one of stringValue() and readerValue() must be set. */
DECL|method|readerValue
specifier|public
name|Reader
name|readerValue
parameter_list|()
block|{
return|return
name|readerValue
return|;
block|}
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|string
parameter_list|,
name|boolean
name|store
parameter_list|,
name|boolean
name|index
parameter_list|,
name|boolean
name|token
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"name cannot be null"
argument_list|)
throw|;
if|if
condition|(
name|string
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"value cannot be null"
argument_list|)
throw|;
name|this
operator|.
name|name
operator|=
name|name
operator|.
name|intern
argument_list|()
expr_stmt|;
comment|// field names are interned
name|this
operator|.
name|stringValue
operator|=
name|string
expr_stmt|;
name|this
operator|.
name|isStored
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|isIndexed
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|isTokenized
operator|=
name|token
expr_stmt|;
block|}
DECL|method|Field
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"name cannot be null"
argument_list|)
throw|;
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"value cannot be null"
argument_list|)
throw|;
name|this
operator|.
name|name
operator|=
name|name
operator|.
name|intern
argument_list|()
expr_stmt|;
comment|// field names are interned
name|this
operator|.
name|readerValue
operator|=
name|reader
expr_stmt|;
block|}
comment|/** True iff the value of the field is to be stored in the index for return     with search hits.  It is an error for this to be true if a field is     Reader-valued. */
DECL|method|isStored
specifier|public
specifier|final
name|boolean
name|isStored
parameter_list|()
block|{
return|return
name|isStored
return|;
block|}
comment|/** True iff the value of the field is to be indexed, so that it may be     searched on. */
DECL|method|isIndexed
specifier|public
specifier|final
name|boolean
name|isIndexed
parameter_list|()
block|{
return|return
name|isIndexed
return|;
block|}
comment|/** True iff the value of the field should be tokenized as text prior to     indexing.  Un-tokenized fields are indexed as a single word and may not be     Reader-valued. */
DECL|method|isTokenized
specifier|public
specifier|final
name|boolean
name|isTokenized
parameter_list|()
block|{
return|return
name|isTokenized
return|;
block|}
comment|/** Prints a Field for human consumption. */
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|isStored
operator|&&
name|isIndexed
operator|&&
operator|!
name|isTokenized
condition|)
return|return
literal|"Keyword<"
operator|+
name|name
operator|+
literal|":"
operator|+
name|stringValue
operator|+
literal|">"
return|;
elseif|else
if|if
condition|(
name|isStored
operator|&&
operator|!
name|isIndexed
operator|&&
operator|!
name|isTokenized
condition|)
return|return
literal|"Unindexed<"
operator|+
name|name
operator|+
literal|":"
operator|+
name|stringValue
operator|+
literal|">"
return|;
elseif|else
if|if
condition|(
name|isStored
operator|&&
name|isIndexed
operator|&&
name|isTokenized
operator|&&
name|stringValue
operator|!=
literal|null
condition|)
return|return
literal|"Text<"
operator|+
name|name
operator|+
literal|":"
operator|+
name|stringValue
operator|+
literal|">"
return|;
elseif|else
if|if
condition|(
operator|!
name|isStored
operator|&&
name|isIndexed
operator|&&
name|isTokenized
operator|&&
name|readerValue
operator|!=
literal|null
condition|)
return|return
literal|"Text<"
operator|+
name|name
operator|+
literal|":"
operator|+
name|readerValue
operator|+
literal|">"
return|;
else|else
return|return
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
