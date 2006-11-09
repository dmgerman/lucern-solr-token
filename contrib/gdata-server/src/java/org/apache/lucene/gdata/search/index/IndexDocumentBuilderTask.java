begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.search.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|index
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|gdata
operator|.
name|data
operator|.
name|ServerBaseEntry
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
name|gdata
operator|.
name|search
operator|.
name|analysis
operator|.
name|ContentStrategy
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
name|gdata
operator|.
name|search
operator|.
name|analysis
operator|.
name|Indexable
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
name|gdata
operator|.
name|search
operator|.
name|analysis
operator|.
name|NotIndexableException
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
name|gdata
operator|.
name|search
operator|.
name|config
operator|.
name|IndexSchema
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
name|gdata
operator|.
name|search
operator|.
name|config
operator|.
name|IndexSchemaField
import|;
end_import
begin_comment
comment|/**  * This callable does all of the entiti processing concurrently while added to  * the {@link org.apache.lucene.gdata.search.index.GDataIndexer} task queue;  *   * @see org.apache.lucene.gdata.search.analysis.Indexable  * @see org.apache.lucene.gdata.search.analysis.ContentStrategy  * @author Simon Willnauer  *   */
end_comment
begin_class
DECL|class|IndexDocumentBuilderTask
class|class
name|IndexDocumentBuilderTask
parameter_list|<
name|T
extends|extends
name|IndexDocument
parameter_list|>
implements|implements
name|IndexDocumentBuilder
argument_list|<
name|T
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|IndexDocumentBuilderTask
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|entry
specifier|private
specifier|final
name|ServerBaseEntry
name|entry
decl_stmt|;
DECL|field|schema
specifier|private
specifier|final
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|action
specifier|private
specifier|final
name|IndexAction
name|action
decl_stmt|;
DECL|field|commitAfter
specifier|private
specifier|final
name|boolean
name|commitAfter
decl_stmt|;
DECL|field|optimizeAfter
specifier|private
specifier|final
name|boolean
name|optimizeAfter
decl_stmt|;
DECL|method|IndexDocumentBuilderTask
specifier|protected
name|IndexDocumentBuilderTask
parameter_list|(
specifier|final
name|ServerBaseEntry
name|entry
parameter_list|,
specifier|final
name|IndexSchema
name|schema
parameter_list|,
name|IndexAction
name|action
parameter_list|,
name|boolean
name|commitAfter
parameter_list|,
name|boolean
name|optimizeAfter
parameter_list|)
block|{
comment|/*          * omit check for null parameter this happens in the controller.          */
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|entry
operator|=
name|entry
expr_stmt|;
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
name|this
operator|.
name|commitAfter
operator|=
name|commitAfter
expr_stmt|;
name|this
operator|.
name|optimizeAfter
operator|=
name|optimizeAfter
expr_stmt|;
block|}
comment|/**      * @see java.util.concurrent.Callable#call()      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|call
specifier|public
name|T
name|call
parameter_list|()
throws|throws
name|GdataIndexerException
block|{
name|Collection
argument_list|<
name|IndexSchemaField
argument_list|>
name|fields
init|=
name|this
operator|.
name|schema
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|GDataIndexDocument
name|document
init|=
operator|new
name|GDataIndexDocument
argument_list|(
name|this
operator|.
name|action
argument_list|,
name|this
operator|.
name|entry
operator|.
name|getId
argument_list|()
argument_list|,
name|this
operator|.
name|entry
operator|.
name|getFeedId
argument_list|()
argument_list|,
name|this
operator|.
name|commitAfter
argument_list|,
name|this
operator|.
name|optimizeAfter
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|action
operator|!=
name|IndexAction
operator|.
name|DELETE
condition|)
block|{
name|int
name|addedFields
init|=
literal|0
decl_stmt|;
for|for
control|(
name|IndexSchemaField
name|field
range|:
name|fields
control|)
block|{
comment|/*              * get the strategy to process the field              */
name|ContentStrategy
name|strategy
init|=
name|ContentStrategy
operator|.
name|getFieldStrategy
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"Process indexable for "
operator|+
name|field
argument_list|)
expr_stmt|;
try|try
block|{
comment|/*                  * get the indexable via the factory method to enable new /                  * different implementation of the interface (this could be a                  * faster dom impl e.g. dom4j)                  */
name|strategy
operator|.
name|processIndexable
argument_list|(
name|Indexable
operator|.
name|getIndexable
argument_list|(
name|this
operator|.
name|entry
argument_list|)
argument_list|)
expr_stmt|;
name|addedFields
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotIndexableException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Can not create field for "
operator|+
name|field
operator|+
literal|" field will be skipped -- reason: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|document
operator|.
name|addField
argument_list|(
name|strategy
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|addedFields
operator|==
literal|0
condition|)
throw|throw
operator|new
name|GdataIndexerException
argument_list|(
literal|"No field added to document for Schema: "
operator|+
name|this
operator|.
name|schema
argument_list|)
throw|;
block|}
return|return
operator|(
name|T
operator|)
name|document
return|;
block|}
block|}
end_class
end_unit
