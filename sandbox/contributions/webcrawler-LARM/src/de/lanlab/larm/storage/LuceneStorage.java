begin_unit
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment
begin_package
DECL|package|de.lanlab.larm.storage
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|storage
package|;
end_package
begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|util
operator|.
name|WebDocument
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
name|*
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
name|*
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
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * FIXME document this class  * Title: LARM Lanlab Retrieval Machine Description: Copyright: Copyright (c)  * Company:  *  * @author    Administrator  * @created   14. Juni 2002  * @version $Id$  */
end_comment
begin_class
DECL|class|LuceneStorage
specifier|public
class|class
name|LuceneStorage
implements|implements
name|DocumentStorage
block|{
DECL|field|INDEX
specifier|public
specifier|final
specifier|static
name|int
name|INDEX
init|=
literal|1
decl_stmt|;
DECL|field|STORE
specifier|public
specifier|final
specifier|static
name|int
name|STORE
init|=
literal|2
decl_stmt|;
DECL|field|TOKEN
specifier|public
specifier|final
specifier|static
name|int
name|TOKEN
init|=
literal|4
decl_stmt|;
DECL|field|fieldInfos
specifier|private
name|HashMap
name|fieldInfos
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|writer
specifier|private
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|indexName
specifier|private
name|String
name|indexName
decl_stmt|;
DECL|field|create
specifier|private
name|boolean
name|create
decl_stmt|;
comment|/**      * Constructor for the LuceneStorage object      */
DECL|method|LuceneStorage
specifier|public
name|LuceneStorage
parameter_list|()
block|{ }
comment|/**      * Sets the analyzer attribute of the LuceneStorage object      *      * @param a  The new analyzer value      */
DECL|method|setAnalyzer
specifier|public
name|void
name|setAnalyzer
parameter_list|(
name|Analyzer
name|a
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|a
expr_stmt|;
block|}
comment|/**      * Sets the indexName attribute of the LuceneStorage object      *      * @param name  The new indexName value      */
DECL|method|setIndexName
specifier|public
name|void
name|setIndexName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|indexName
operator|=
name|name
expr_stmt|;
block|}
comment|/**      * Sets the fieldInfo attribute of the LuceneStorage object      *      * @param fieldName  The new fieldInfo value      * @param value      The new fieldInfo value      */
DECL|method|setFieldInfo
specifier|public
name|void
name|setFieldInfo
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
name|value
parameter_list|)
block|{
name|fieldInfos
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
operator|new
name|Integer
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the create attribute of the LuceneStorage object      *      * @param create  The new create value      */
DECL|method|setCreate
specifier|public
name|void
name|setCreate
parameter_list|(
name|boolean
name|create
parameter_list|)
block|{
name|this
operator|.
name|create
operator|=
name|create
expr_stmt|;
block|}
comment|/**      * Description of the Method      */
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
block|{
comment|// FIXME: replace with logging
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"opening Lucene storage with index name "
operator|+
name|indexName
operator|+
literal|")"
argument_list|)
expr_stmt|;
try|try
block|{
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|indexName
argument_list|,
name|analyzer
argument_list|,
name|create
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// FIXME: replace with logging
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"IOException occured when opening Lucene Index with index name '"
operator|+
name|indexName
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
comment|// FIXME: replace with logging
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"lucene storage opened successfully"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Gets the fieldInfo attribute of the LuceneStorage object      *      * @param fieldName  Description of the Parameter      * @param defaultValue Description of the Parameter      * @return           The fieldInfo value      */
DECL|method|getFieldInfo
specifier|protected
name|int
name|getFieldInfo
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
name|defaultValue
parameter_list|)
block|{
name|Integer
name|info
init|=
operator|(
name|Integer
operator|)
name|fieldInfos
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
return|return
name|info
operator|.
name|intValue
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|defaultValue
return|;
block|}
block|}
DECL|method|addField
specifier|protected
name|void
name|addField
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|int
name|defaultIndexFlags
parameter_list|)
block|{
name|int
name|flags
init|=
name|getFieldInfo
argument_list|(
name|name
argument_list|,
name|defaultIndexFlags
argument_list|)
decl_stmt|;
if|if
condition|(
name|flags
operator|!=
literal|0
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
operator|(
name|flags
operator|&
name|STORE
operator|)
operator|!=
literal|0
argument_list|,
operator|(
name|flags
operator|&
name|INDEX
operator|)
operator|!=
literal|0
argument_list|,
operator|(
name|flags
operator|&
name|TOKEN
operator|)
operator|!=
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Description of the Method      *      * @param webDoc  Description of the Parameter      * @return        Description of the Return Value      */
DECL|method|store
specifier|public
name|WebDocument
name|store
parameter_list|(
name|WebDocument
name|webDoc
parameter_list|)
block|{
comment|//System.out.println("storing " + webDoc.getUrl());
name|boolean
name|store
init|=
literal|false
decl_stmt|;
name|boolean
name|index
init|=
literal|false
decl_stmt|;
name|boolean
name|token
init|=
literal|false
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|int
name|flags
decl_stmt|;
name|addField
argument_list|(
name|doc
argument_list|,
literal|"url"
argument_list|,
name|webDoc
operator|.
name|getUrl
argument_list|()
operator|.
name|toExternalForm
argument_list|()
argument_list|,
name|STORE
operator||
name|INDEX
argument_list|)
expr_stmt|;
name|addField
argument_list|(
name|doc
argument_list|,
literal|"mimetype"
argument_list|,
name|webDoc
operator|.
name|getMimeType
argument_list|()
argument_list|,
name|STORE
operator||
name|INDEX
argument_list|)
expr_stmt|;
comment|// addField(doc, "...", webDoc.getNormalizedURLString(), STORE | INDEX); and so fortg
comment|// todo: other fields
name|Set
name|fields
init|=
name|webDoc
operator|.
name|getFieldNames
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|it
init|=
name|fields
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|fieldName
init|=
operator|(
name|String
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|Object
name|field
init|=
name|webDoc
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|instanceof
name|char
index|[]
condition|)
block|{
name|addField
argument_list|(
name|doc
argument_list|,
name|fieldName
argument_list|,
operator|new
name|String
argument_list|(
operator|(
name|char
index|[]
operator|)
name|field
argument_list|)
argument_list|,
name|STORE
operator||
name|INDEX
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|field
operator|instanceof
name|String
condition|)
block|{
name|addField
argument_list|(
name|doc
argument_list|,
name|fieldName
argument_list|,
operator|(
name|String
operator|)
name|field
argument_list|,
name|STORE
operator||
name|INDEX
argument_list|)
expr_stmt|;
block|}
comment|/* else ? */
block|}
try|try
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// FIXME: replace with logging
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"IOException occured when adding document to Lucene index"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
name|webDoc
return|;
block|}
comment|//public void set
block|}
end_class
end_unit
