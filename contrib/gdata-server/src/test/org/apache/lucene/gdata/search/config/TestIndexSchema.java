begin_unit
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.search.config
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
name|config
package|;
end_package
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
name|PerFieldAnalyzerWrapper
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
name|StopAnalyzer
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
name|standard
operator|.
name|StandardAnalyzer
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
name|search
operator|.
name|index
operator|.
name|IndexDocument
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
begin_comment
comment|/**  * @author Simon Willnauer  *  */
end_comment
begin_class
DECL|class|TestIndexSchema
specifier|public
class|class
name|TestIndexSchema
extends|extends
name|TestCase
block|{
DECL|field|schema
name|IndexSchema
name|schema
decl_stmt|;
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|schema
operator|=
operator|new
name|IndexSchema
argument_list|()
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
comment|/*      * Test method for      * 'org.apache.lucene.gdata.search.config.IndexSchema.initialize()'      */
DECL|method|testInitialize
specifier|public
name|void
name|testInitialize
parameter_list|()
block|{
try|try
block|{
name|schema
operator|.
name|initialize
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"def search field is null"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// TODO: handle exception
block|}
name|schema
operator|.
name|setDefaultSearchField
argument_list|(
literal|"someField"
argument_list|)
expr_stmt|;
try|try
block|{
name|schema
operator|.
name|initialize
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"name is null"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// TODO: handle exception
block|}
name|schema
operator|.
name|setName
argument_list|(
literal|"someName"
argument_list|)
expr_stmt|;
try|try
block|{
name|schema
operator|.
name|initialize
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"indexLocation  is null"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// TODO: handle exception
block|}
name|schema
operator|.
name|setIndexLocation
argument_list|(
literal|"/loc/loc"
argument_list|)
expr_stmt|;
try|try
block|{
name|schema
operator|.
name|initialize
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"default search field is not set as a field"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// TODO: handle exception
block|}
name|IndexSchemaField
name|f
init|=
operator|new
name|IndexSchemaField
argument_list|()
decl_stmt|;
name|f
operator|.
name|setName
argument_list|(
name|schema
operator|.
name|getDefaultSearchField
argument_list|()
argument_list|)
expr_stmt|;
name|f
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
name|f
argument_list|)
expr_stmt|;
try|try
block|{
name|schema
operator|.
name|initialize
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"field check failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// TODO: handle exception
block|}
name|f
operator|.
name|setPath
argument_list|(
literal|"path"
argument_list|)
expr_stmt|;
name|schema
operator|.
name|initialize
argument_list|()
expr_stmt|;
block|}
comment|/*      * Test method for      * 'org.apache.lucene.gdata.search.config.IndexSchema.addSchemaField(IndexSchemaField)'      */
DECL|method|testAddSchemaField
specifier|public
name|void
name|testAddSchemaField
parameter_list|()
block|{
name|schema
operator|.
name|addSchemaField
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|schema
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSchemaField
name|f
init|=
operator|new
name|IndexSchemaField
argument_list|()
decl_stmt|;
name|f
operator|.
name|setName
argument_list|(
name|IndexDocument
operator|.
name|FIELD_ENTRY_ID
argument_list|)
expr_stmt|;
name|schema
operator|.
name|addSchemaField
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|schema
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|setName
argument_list|(
name|IndexDocument
operator|.
name|FIELD_FEED_ID
argument_list|)
expr_stmt|;
name|schema
operator|.
name|addSchemaField
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|schema
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|setName
argument_list|(
literal|"some"
argument_list|)
expr_stmt|;
name|schema
operator|.
name|addSchemaField
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|schema
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StandardAnalyzer
operator|.
name|class
argument_list|,
name|schema
operator|.
name|getServiceAnalyzer
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StandardAnalyzer
operator|.
name|class
argument_list|,
name|schema
operator|.
name|getSchemaAnalyzer
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|setName
argument_list|(
literal|"someOther"
argument_list|)
expr_stmt|;
name|f
operator|.
name|setAnalyzerClass
argument_list|(
name|StopAnalyzer
operator|.
name|class
argument_list|)
expr_stmt|;
name|schema
operator|.
name|addSchemaField
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|schema
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PerFieldAnalyzerWrapper
operator|.
name|class
argument_list|,
name|schema
operator|.
name|getSchemaAnalyzer
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|schema
operator|.
name|addSchemaField
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|schema
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*      * Test method for      * 'org.apache.lucene.gdata.search.config.IndexSchema.getSearchableFieldNames()'      */
DECL|method|testGetSearchableFieldNames
specifier|public
name|void
name|testGetSearchableFieldNames
parameter_list|()
block|{
name|IndexSchemaField
name|f
init|=
operator|new
name|IndexSchemaField
argument_list|()
decl_stmt|;
name|f
operator|.
name|setName
argument_list|(
literal|"some"
argument_list|)
expr_stmt|;
name|schema
operator|.
name|addSchemaField
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|schema
operator|.
name|getSearchableFieldNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|getSearchableFieldNames
argument_list|()
operator|.
name|contains
argument_list|(
literal|"some"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|schema
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|schema
operator|.
name|equals
argument_list|(
operator|new
name|String
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|equals
argument_list|(
name|schema
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|schema
operator|.
name|equals
argument_list|(
operator|new
name|IndexSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|IndexSchema
name|s1
init|=
operator|new
name|IndexSchema
argument_list|()
decl_stmt|;
name|s1
operator|.
name|setName
argument_list|(
literal|"someName"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|schema
operator|.
name|equals
argument_list|(
name|s1
argument_list|)
argument_list|)
expr_stmt|;
name|schema
operator|.
name|setName
argument_list|(
name|s1
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|equals
argument_list|(
name|s1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testHashCode
specifier|public
name|void
name|testHashCode
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|schema
operator|.
name|hashCode
argument_list|()
argument_list|,
name|schema
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|schema
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSchema
name|s1
init|=
operator|new
name|IndexSchema
argument_list|()
decl_stmt|;
name|s1
operator|.
name|setName
argument_list|(
literal|"someName"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|hashCode
argument_list|()
operator|!=
name|s1
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|schema
operator|.
name|setName
argument_list|(
name|s1
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|hashCode
argument_list|()
operator|==
name|s1
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testToSTringNoNullPEx
specifier|public
name|void
name|testToSTringNoNullPEx
parameter_list|()
block|{
name|assertNotNull
argument_list|(
name|schema
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
