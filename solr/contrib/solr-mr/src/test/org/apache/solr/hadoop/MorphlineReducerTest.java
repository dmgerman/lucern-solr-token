begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|hadoop
package|;
end_package
begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|Text
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|TaskID
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|InputFormat
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|InputSplit
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|JobContext
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|RecordReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskAttemptContext
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskAttemptID
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mrunit
operator|.
name|mapreduce
operator|.
name|ReduceDriver
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
name|LuceneTestCase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|AbstractZkTestCase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import
begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import
begin_class
DECL|class|MorphlineReducerTest
specifier|public
class|class
name|MorphlineReducerTest
extends|extends
name|MRUnitBase
block|{
DECL|class|MySolrReducer
specifier|public
specifier|static
class|class
name|MySolrReducer
extends|extends
name|SolrReducer
block|{
DECL|field|context
name|Context
name|context
decl_stmt|;
annotation|@
name|Override
DECL|method|setup
specifier|protected
name|void
name|setup
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
comment|// handle a bug in MRUnit - should be fixed in MRUnit 1.0.0
name|when
argument_list|(
name|context
operator|.
name|getTaskAttemptID
argument_list|()
argument_list|)
operator|.
name|thenAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|TaskAttemptID
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TaskAttemptID
name|answer
parameter_list|(
specifier|final
name|InvocationOnMock
name|invocation
parameter_list|)
block|{
comment|// FIXME MRUNIT seems to pass taskid to the reduce task as mapred.TaskID rather than mapreduce.TaskID
return|return
operator|new
name|TaskAttemptID
argument_list|(
operator|new
name|TaskID
argument_list|(
literal|"000000000000"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|super
operator|.
name|setup
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|NullInputFormat
specifier|public
specifier|static
class|class
name|NullInputFormat
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|InputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
annotation|@
name|Override
DECL|method|getSplits
specifier|public
name|List
argument_list|<
name|InputSplit
argument_list|>
name|getSplits
parameter_list|(
name|JobContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|Lists
operator|.
name|newArrayList
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createRecordReader
specifier|public
name|RecordReader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|createRecordReader
parameter_list|(
name|InputSplit
name|split
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReducer
specifier|public
name|void
name|testReducer
parameter_list|()
throws|throws
name|Exception
block|{
name|MySolrReducer
name|myReducer
init|=
operator|new
name|MySolrReducer
argument_list|()
decl_stmt|;
name|ReduceDriver
argument_list|<
name|Text
argument_list|,
name|SolrInputDocumentWritable
argument_list|,
name|Text
argument_list|,
name|SolrInputDocumentWritable
argument_list|>
name|reduceDriver
init|=
name|ReduceDriver
operator|.
name|newReduceDriver
argument_list|(
name|myReducer
argument_list|)
decl_stmt|;
name|Configuration
name|config
init|=
name|reduceDriver
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|setupHadoopConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|SolrInputDocumentWritable
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|SolrInputDocumentWritable
argument_list|>
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|sid
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|String
name|id
init|=
literal|"myid1"
decl_stmt|;
name|sid
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|sid
operator|.
name|addField
argument_list|(
literal|"text"
argument_list|,
literal|"some unique text"
argument_list|)
expr_stmt|;
name|SolrInputDocumentWritable
name|sidw
init|=
operator|new
name|SolrInputDocumentWritable
argument_list|(
name|sid
argument_list|)
decl_stmt|;
name|values
operator|.
name|add
argument_list|(
name|sidw
argument_list|)
expr_stmt|;
name|reduceDriver
operator|.
name|withInput
argument_list|(
operator|new
name|Text
argument_list|(
name|id
argument_list|)
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|reduceDriver
operator|.
name|withCacheArchive
argument_list|(
name|solrHomeZip
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|reduceDriver
operator|.
name|withOutputFormat
argument_list|(
name|SolrOutputFormat
operator|.
name|class
argument_list|,
name|NullInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|reduceDriver
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected 1 counter increment"
argument_list|,
literal|1
argument_list|,
name|reduceDriver
operator|.
name|getCounters
argument_list|()
operator|.
name|findCounter
argument_list|(
name|SolrCounters
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|SolrCounters
operator|.
name|DOCUMENTS_WRITTEN
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
