begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
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
name|Document
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
name|Field
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
name|Field
operator|.
name|Index
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
name|Field
operator|.
name|Store
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
name|Field
operator|.
name|TermVector
import|;
end_import
begin_comment
comment|/**  * A DocMaker reading one line at a time as a Document from a single file. This  * saves IO cost (over DirContentSource) of recursing through a directory and  * opening a new file for every document. It also re-uses its Document and Field  * instance to improve indexing speed.<br>  * The expected format of each line is (arguments are separated by&lt;TAB&gt;):  *<i>title, date, body</i>. If a line is read in a different format, a  * {@link RuntimeException} will be thrown. In general, you should use this doc  * maker with files that were created with {@link WriteLineDocTask}.<br>  *<br>  * Config properties:  *<ul>  *<li>doc.random.id.limit=N (default -1) -- create random docid in the range  * 0..N; this is useful with UpdateDoc to test updating random documents; if  * this is unspecified or -1, then docid is sequentially assigned  *</ul>  */
end_comment
begin_class
DECL|class|LineDocMaker
specifier|public
class|class
name|LineDocMaker
extends|extends
name|DocMaker
block|{
DECL|field|r
specifier|private
name|Random
name|r
decl_stmt|;
DECL|field|numDocs
specifier|private
name|int
name|numDocs
decl_stmt|;
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|DocState
name|ds
init|=
name|reuseFields
condition|?
name|getDocState
argument_list|()
else|:
name|localDocState
decl_stmt|;
name|DocData
name|dd
init|=
name|source
operator|.
name|getNextDocData
argument_list|(
name|ds
operator|.
name|docData
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|reuseFields
condition|?
name|ds
operator|.
name|doc
else|:
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Field
name|body
init|=
name|ds
operator|.
name|getField
argument_list|(
name|BODY_FIELD
argument_list|,
name|storeVal
argument_list|,
name|bodyIndexVal
argument_list|,
name|termVecVal
argument_list|)
decl_stmt|;
name|body
operator|.
name|setValue
argument_list|(
name|dd
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|Field
name|title
init|=
name|ds
operator|.
name|getField
argument_list|(
name|TITLE_FIELD
argument_list|,
name|storeVal
argument_list|,
name|indexVal
argument_list|,
name|termVecVal
argument_list|)
decl_stmt|;
name|title
operator|.
name|setValue
argument_list|(
name|dd
operator|.
name|getTitle
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|title
argument_list|)
expr_stmt|;
name|Field
name|date
init|=
name|ds
operator|.
name|getField
argument_list|(
name|DATE_FIELD
argument_list|,
name|storeVal
argument_list|,
name|indexVal
argument_list|,
name|termVecVal
argument_list|)
decl_stmt|;
name|date
operator|.
name|setValue
argument_list|(
name|dd
operator|.
name|getDate
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|date
argument_list|)
expr_stmt|;
name|String
name|docID
init|=
literal|"doc"
operator|+
operator|(
name|r
operator|!=
literal|null
condition|?
name|r
operator|.
name|nextInt
argument_list|(
name|numDocs
argument_list|)
else|:
name|incrNumDocsCreated
argument_list|()
operator|)
decl_stmt|;
name|Field
name|id
init|=
name|ds
operator|.
name|getField
argument_list|(
name|ID_FIELD
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED_NO_NORMS
argument_list|,
name|TermVector
operator|.
name|NO
argument_list|)
decl_stmt|;
name|id
operator|.
name|setValue
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"cannot change document size with LineDocMaker"
argument_list|)
throw|;
block|}
DECL|method|setConfig
specifier|public
name|void
name|setConfig
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|source
operator|=
operator|new
name|LineDocSource
argument_list|()
expr_stmt|;
name|source
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|numDocs
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.random.id.limit"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|numDocs
operator|!=
operator|-
literal|1
condition|)
block|{
name|r
operator|=
operator|new
name|Random
argument_list|(
literal|179
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
