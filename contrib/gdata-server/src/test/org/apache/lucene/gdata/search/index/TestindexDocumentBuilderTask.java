begin_unit
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
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
name|concurrent
operator|.
name|ExecutionException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
operator|.
name|ContentType
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
name|utils
operator|.
name|ProvidedServiceStub
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|PlainTextConstruct
import|;
end_import
begin_comment
comment|/**  * @author Simon Willnauer  *  */
end_comment
begin_class
DECL|class|TestindexDocumentBuilderTask
specifier|public
class|class
name|TestindexDocumentBuilderTask
extends|extends
name|TestCase
block|{
DECL|field|fineBuilder
name|IndexDocumentBuilder
name|fineBuilder
decl_stmt|;
DECL|field|failInStrategyBuilder
name|IndexDocumentBuilder
name|failInStrategyBuilder
decl_stmt|;
DECL|field|builder
name|IndexDocumentBuilder
name|builder
decl_stmt|;
DECL|field|zeroFields
name|IndexDocumentBuilderTask
name|zeroFields
decl_stmt|;
DECL|field|ID
specifier|static
name|String
name|ID
init|=
literal|"someId"
decl_stmt|;
DECL|field|CONTENT_FIELD
specifier|static
name|String
name|CONTENT_FIELD
init|=
literal|"someId"
decl_stmt|;
DECL|field|CONTENT
specifier|static
name|String
name|CONTENT
init|=
literal|"foo bar"
decl_stmt|;
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|ServerBaseEntry
name|entry
init|=
operator|new
name|ServerBaseEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setVersionId
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setFeedId
argument_list|(
literal|"myFeed"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setId
argument_list|(
name|ID
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setContent
argument_list|(
operator|new
name|PlainTextConstruct
argument_list|(
name|CONTENT
argument_list|)
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setServiceConfig
argument_list|(
operator|new
name|ProvidedServiceStub
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSchema
name|schema
init|=
operator|new
name|IndexSchema
argument_list|()
decl_stmt|;
name|schema
operator|.
name|setName
argument_list|(
literal|"mySchema"
argument_list|)
expr_stmt|;
name|IndexSchemaField
name|field
init|=
operator|new
name|IndexSchemaField
argument_list|()
decl_stmt|;
name|field
operator|.
name|setName
argument_list|(
name|CONTENT_FIELD
argument_list|)
expr_stmt|;
name|field
operator|.
name|setPath
argument_list|(
literal|"/entry/content"
argument_list|)
expr_stmt|;
name|field
operator|.
name|setContentType
argument_list|(
name|ContentType
operator|.
name|TEXT
argument_list|)
expr_stmt|;
name|schema
operator|.
name|addSchemaField
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|fineBuilder
operator|=
operator|new
name|IndexDocumentBuilderTask
argument_list|(
name|entry
argument_list|,
name|schema
argument_list|,
name|IndexAction
operator|.
name|INSERT
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|/*          * two fields, one will fail due to broken xpath.          * One will remain.          */
name|schema
operator|=
operator|new
name|IndexSchema
argument_list|()
expr_stmt|;
name|schema
operator|.
name|setName
argument_list|(
literal|"mySchema"
argument_list|)
expr_stmt|;
name|field
operator|=
operator|new
name|IndexSchemaField
argument_list|()
expr_stmt|;
name|field
operator|.
name|setName
argument_list|(
literal|"someContent"
argument_list|)
expr_stmt|;
comment|//broken xpath
name|field
operator|.
name|setPath
argument_list|(
literal|"/entry///wrongXPath"
argument_list|)
expr_stmt|;
name|field
operator|.
name|setContentType
argument_list|(
name|ContentType
operator|.
name|TEXT
argument_list|)
expr_stmt|;
name|schema
operator|.
name|addSchemaField
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|=
operator|new
name|IndexSchemaField
argument_list|()
expr_stmt|;
name|field
operator|.
name|setName
argument_list|(
name|CONTENT_FIELD
argument_list|)
expr_stmt|;
name|field
operator|.
name|setPath
argument_list|(
literal|"/entry/content"
argument_list|)
expr_stmt|;
name|field
operator|.
name|setContentType
argument_list|(
name|ContentType
operator|.
name|TEXT
argument_list|)
expr_stmt|;
name|schema
operator|.
name|addSchemaField
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|failInStrategyBuilder
operator|=
operator|new
name|IndexDocumentBuilderTask
argument_list|(
name|entry
argument_list|,
name|schema
argument_list|,
name|IndexAction
operator|.
name|INSERT
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//fail with no fields
name|schema
operator|=
operator|new
name|IndexSchema
argument_list|()
expr_stmt|;
name|schema
operator|.
name|setName
argument_list|(
literal|"mySchema"
argument_list|)
expr_stmt|;
name|this
operator|.
name|zeroFields
operator|=
operator|new
name|IndexDocumentBuilderTask
argument_list|(
name|entry
argument_list|,
name|schema
argument_list|,
name|IndexAction
operator|.
name|INSERT
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/*      * Test method for 'org.apache.lucene.gdata.search.index.IndexDocumentBuilderTask.IndexDocumentBuilderTask(ServerBaseEntry, IndexSchema, IndexAction, boolean)'      */
DECL|method|testIndexDocumentBuilderTask
specifier|public
name|void
name|testIndexDocumentBuilderTask
parameter_list|()
block|{
name|IndexDocument
name|doc
init|=
name|this
operator|.
name|fineBuilder
operator|.
name|call
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
operator|.
name|getDeletealbe
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|doc
operator|.
name|getWriteable
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexDocument
operator|.
name|FIELD_ENTRY_ID
argument_list|,
name|doc
operator|.
name|getDeletealbe
argument_list|()
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ID
argument_list|,
name|doc
operator|.
name|getDeletealbe
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ID
argument_list|,
name|doc
operator|.
name|getWriteable
argument_list|()
operator|.
name|getField
argument_list|(
name|IndexDocument
operator|.
name|FIELD_ENTRY_ID
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|doc
operator|.
name|getWriteable
argument_list|()
operator|.
name|getField
argument_list|(
name|CONTENT_FIELD
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
comment|/*          * the broken xpath fails but the other fields will be indexed          */
name|doc
operator|=
name|this
operator|.
name|failInStrategyBuilder
operator|.
name|call
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|doc
operator|.
name|getDeletealbe
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|doc
operator|.
name|getWriteable
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexDocument
operator|.
name|FIELD_ENTRY_ID
argument_list|,
name|doc
operator|.
name|getDeletealbe
argument_list|()
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ID
argument_list|,
name|doc
operator|.
name|getDeletealbe
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ID
argument_list|,
name|doc
operator|.
name|getWriteable
argument_list|()
operator|.
name|getField
argument_list|(
name|IndexDocument
operator|.
name|FIELD_ENTRY_ID
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|doc
operator|.
name|getWriteable
argument_list|()
operator|.
name|getField
argument_list|(
name|CONTENT_FIELD
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|zeroFields
operator|.
name|call
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"zero fields in document"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GdataIndexerException
name|e
parameter_list|)
block|{}
block|}
comment|/*      * Test method for 'org.apache.lucene.gdata.search.index.IndexDocumentBuilderTask.call()'      */
DECL|method|testCall
specifier|public
name|void
name|testCall
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
block|{
name|ExecutorService
name|service
init|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
decl_stmt|;
name|Future
argument_list|<
name|IndexDocument
argument_list|>
name|future
init|=
name|service
operator|.
name|submit
argument_list|(
name|this
operator|.
name|fineBuilder
argument_list|)
decl_stmt|;
name|IndexDocument
name|doc
init|=
name|future
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
operator|.
name|getDeletealbe
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|doc
operator|.
name|getWriteable
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexDocument
operator|.
name|FIELD_ENTRY_ID
argument_list|,
name|doc
operator|.
name|getDeletealbe
argument_list|()
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ID
argument_list|,
name|doc
operator|.
name|getDeletealbe
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ID
argument_list|,
name|doc
operator|.
name|getWriteable
argument_list|()
operator|.
name|getField
argument_list|(
name|IndexDocument
operator|.
name|FIELD_ENTRY_ID
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|doc
operator|.
name|getWriteable
argument_list|()
operator|.
name|getField
argument_list|(
name|CONTENT_FIELD
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|commitAfter
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|optimizeAfter
argument_list|()
argument_list|)
expr_stmt|;
comment|/*          * the broken xpath fails but the other fields will be indexed          */
name|future
operator|=
name|service
operator|.
name|submit
argument_list|(
name|this
operator|.
name|failInStrategyBuilder
argument_list|)
expr_stmt|;
name|doc
operator|=
name|future
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|doc
operator|.
name|getDeletealbe
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|doc
operator|.
name|getWriteable
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexDocument
operator|.
name|FIELD_ENTRY_ID
argument_list|,
name|doc
operator|.
name|getDeletealbe
argument_list|()
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ID
argument_list|,
name|doc
operator|.
name|getDeletealbe
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ID
argument_list|,
name|doc
operator|.
name|getWriteable
argument_list|()
operator|.
name|getField
argument_list|(
name|IndexDocument
operator|.
name|FIELD_ENTRY_ID
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|doc
operator|.
name|getWriteable
argument_list|()
operator|.
name|getField
argument_list|(
name|CONTENT_FIELD
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|future
operator|=
name|service
operator|.
name|submit
argument_list|(
name|this
operator|.
name|zeroFields
argument_list|)
expr_stmt|;
try|try
block|{
name|future
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"zero fields in document"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getClass
argument_list|()
operator|==
name|GdataIndexerException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|service
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
