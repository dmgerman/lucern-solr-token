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
name|util
operator|.
name|Enumeration
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
name|ArrayList
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
comment|// for javadoc
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
name|Hits
import|;
end_import
begin_comment
comment|// for javadoc
end_comment
begin_comment
comment|/** Documents are the unit of indexing and search.  *  * A Document is a set of fields.  Each field has a name and a textual value.  * A field may be stored with the document, in which case it is returned with  * search hits on the document.  Thus each document should typically contain  * stored fields which uniquely identify it.  * */
end_comment
begin_class
DECL|class|Document
specifier|public
specifier|final
class|class
name|Document
implements|implements
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
DECL|field|fieldList
name|DocumentFieldList
name|fieldList
init|=
literal|null
decl_stmt|;
DECL|field|boost
specifier|private
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
comment|/** Constructs a new document with no fields. */
DECL|method|Document
specifier|public
name|Document
parameter_list|()
block|{}
comment|/** Sets a boost factor for hits on any field of this document.  This value    * will be multiplied into the score of all hits on this document.    *    *<p>Values are multiplied into the value of {@link Field#getBoost()} of    * each field in this document.  Thus, this method in effect sets a default    * boost for the fields of this document.    *    * @see Field#setBoost(float)    */
DECL|method|setBoost
specifier|public
name|void
name|setBoost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
block|}
comment|/** Returns the boost factor for hits on any field of this document.    *    *<p>The default value is 1.0.    *    *<p>Note: This value is not stored directly with the document in the index.    * Documents returned from {@link IndexReader#document(int)} and    * {@link Hits#doc(int)} may thus not have the same value present as when    * this document was indexed.    *    * @see #setBoost(float)    */
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
comment|/** Adds a field to a document.  Several fields may be added with    * the same name.  In this case, if the fields are indexed, their text is    * treated as though appended for the purposes of search. */
DECL|method|add
specifier|public
specifier|final
name|void
name|add
parameter_list|(
name|Field
name|field
parameter_list|)
block|{
name|fieldList
operator|=
operator|new
name|DocumentFieldList
argument_list|(
name|field
argument_list|,
name|fieldList
argument_list|)
expr_stmt|;
block|}
comment|/** Returns a field with the given name if any exist in this document, or     null.  If multiple fields exists with this name, this method returns the     last field value added. */
DECL|method|getField
specifier|public
specifier|final
name|Field
name|getField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|DocumentFieldList
name|list
init|=
name|fieldList
init|;
name|list
operator|!=
literal|null
condition|;
name|list
operator|=
name|list
operator|.
name|next
control|)
if|if
condition|(
name|list
operator|.
name|field
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
return|return
name|list
operator|.
name|field
return|;
return|return
literal|null
return|;
block|}
comment|/** Returns the string value of the field with the given name if any exist in     this document, or null.  If multiple fields exist with this name, this     method returns the last value added. */
DECL|method|get
specifier|public
specifier|final
name|String
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Field
name|field
init|=
name|getField
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
return|return
name|field
operator|.
name|stringValue
argument_list|()
return|;
else|else
return|return
literal|null
return|;
block|}
comment|/** Returns an Enumeration of all the fields in a document. */
DECL|method|fields
specifier|public
specifier|final
name|Enumeration
name|fields
parameter_list|()
block|{
return|return
operator|new
name|DocumentFieldEnumeration
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Returns an array of {@link Field}s with the given name.    * This method can return<code>null</code>.    *    * @param name the name of the field    * @return a<code>Field[]</code> array    */
DECL|method|getFields
specifier|public
specifier|final
name|Field
index|[]
name|getFields
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|List
name|tempFieldList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|DocumentFieldList
name|list
init|=
name|fieldList
init|;
name|list
operator|!=
literal|null
condition|;
name|list
operator|=
name|list
operator|.
name|next
control|)
block|{
if|if
condition|(
name|list
operator|.
name|field
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|tempFieldList
operator|.
name|add
argument_list|(
name|list
operator|.
name|field
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|fieldCount
init|=
name|tempFieldList
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldCount
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|(
name|Field
index|[]
operator|)
name|tempFieldList
operator|.
name|toArray
argument_list|(
operator|new
name|Field
index|[]
block|{}
argument_list|)
return|;
block|}
block|}
comment|/**    * Returns an array of values of the field specified as the method parameter.    * This method can return<code>null</code>.    * UnStored fields' values cannot be returned by this method.    *    * @param name the name of the field    * @return a<code>String[]</code> of field values    */
DECL|method|getValues
specifier|public
specifier|final
name|String
index|[]
name|getValues
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Field
index|[]
name|namedFields
init|=
name|getFields
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|namedFields
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|String
index|[]
name|values
init|=
operator|new
name|String
index|[
name|namedFields
operator|.
name|length
index|]
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
name|namedFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|namedFields
index|[
name|i
index|]
operator|.
name|stringValue
argument_list|()
expr_stmt|;
block|}
return|return
name|values
return|;
block|}
comment|/** Prints the fields of a document for human consumption. */
DECL|method|toString
specifier|public
specifier|final
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
literal|"Document<"
argument_list|)
expr_stmt|;
for|for
control|(
name|DocumentFieldList
name|list
init|=
name|fieldList
init|;
name|list
operator|!=
literal|null
condition|;
name|list
operator|=
name|list
operator|.
name|next
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|list
operator|.
name|field
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|list
operator|.
name|next
operator|!=
literal|null
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
begin_class
DECL|class|DocumentFieldList
specifier|final
class|class
name|DocumentFieldList
implements|implements
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
DECL|method|DocumentFieldList
name|DocumentFieldList
parameter_list|(
name|Field
name|f
parameter_list|,
name|DocumentFieldList
name|n
parameter_list|)
block|{
name|field
operator|=
name|f
expr_stmt|;
name|next
operator|=
name|n
expr_stmt|;
block|}
DECL|field|field
name|Field
name|field
decl_stmt|;
DECL|field|next
name|DocumentFieldList
name|next
decl_stmt|;
block|}
end_class
begin_class
DECL|class|DocumentFieldEnumeration
specifier|final
class|class
name|DocumentFieldEnumeration
implements|implements
name|Enumeration
block|{
DECL|field|fields
name|DocumentFieldList
name|fields
decl_stmt|;
DECL|method|DocumentFieldEnumeration
name|DocumentFieldEnumeration
parameter_list|(
name|Document
name|d
parameter_list|)
block|{
name|fields
operator|=
name|d
operator|.
name|fieldList
expr_stmt|;
block|}
DECL|method|hasMoreElements
specifier|public
specifier|final
name|boolean
name|hasMoreElements
parameter_list|()
block|{
return|return
name|fields
operator|==
literal|null
condition|?
literal|false
else|:
literal|true
return|;
block|}
DECL|method|nextElement
specifier|public
specifier|final
name|Object
name|nextElement
parameter_list|()
block|{
name|Field
name|result
init|=
name|fields
operator|.
name|field
decl_stmt|;
name|fields
operator|=
name|fields
operator|.
name|next
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
