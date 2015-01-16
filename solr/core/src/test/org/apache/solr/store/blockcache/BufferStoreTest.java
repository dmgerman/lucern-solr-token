begin_unit
begin_package
DECL|package|org.apache.solr.store.blockcache
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|store
operator|.
name|blockcache
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
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
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
begin_class
DECL|class|BufferStoreTest
specifier|public
class|class
name|BufferStoreTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|blockSize
specifier|private
specifier|final
specifier|static
name|int
name|blockSize
init|=
literal|1024
decl_stmt|;
DECL|field|metrics
specifier|private
name|Metrics
name|metrics
decl_stmt|;
DECL|field|store
specifier|private
name|Store
name|store
decl_stmt|;
annotation|@
name|Before
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|metrics
operator|=
operator|new
name|Metrics
argument_list|()
expr_stmt|;
name|BufferStore
operator|.
name|initNewBuffer
argument_list|(
name|blockSize
argument_list|,
name|blockSize
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
name|store
operator|=
name|BufferStore
operator|.
name|instance
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBufferTakePut
specifier|public
name|void
name|testBufferTakePut
parameter_list|()
block|{
name|byte
index|[]
name|b1
init|=
name|store
operator|.
name|takeBuffer
argument_list|(
name|blockSize
argument_list|)
decl_stmt|;
name|assertGaugeMetricsChanged
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|byte
index|[]
name|b2
init|=
name|store
operator|.
name|takeBuffer
argument_list|(
name|blockSize
argument_list|)
decl_stmt|;
name|byte
index|[]
name|b3
init|=
name|store
operator|.
name|takeBuffer
argument_list|(
name|blockSize
argument_list|)
decl_stmt|;
name|assertRawMetricCounts
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertGaugeMetricsChanged
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|store
operator|.
name|putBuffer
argument_list|(
name|b1
argument_list|)
expr_stmt|;
name|assertGaugeMetricsChanged
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|store
operator|.
name|putBuffer
argument_list|(
name|b2
argument_list|)
expr_stmt|;
name|store
operator|.
name|putBuffer
argument_list|(
name|b3
argument_list|)
expr_stmt|;
name|assertRawMetricCounts
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertGaugeMetricsChanged
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|assertRawMetricCounts
specifier|private
name|void
name|assertRawMetricCounts
parameter_list|(
name|int
name|allocated
parameter_list|,
name|int
name|lost
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Buffer allocation count is wrong."
argument_list|,
name|allocated
argument_list|,
name|metrics
operator|.
name|shardBuffercacheAllocate
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Lost buffer count is wrong"
argument_list|,
name|lost
argument_list|,
name|metrics
operator|.
name|shardBuffercacheLost
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Stateful method to verify whether the amount of buffers allocated and lost    * since the last call has changed.    *    * @param allocated    *          whether buffers should have been allocated since the last call    * @param lost    *          whether buffers should have been lost since the last call    */
DECL|method|assertGaugeMetricsChanged
specifier|private
name|void
name|assertGaugeMetricsChanged
parameter_list|(
name|boolean
name|allocated
parameter_list|,
name|boolean
name|lost
parameter_list|)
block|{
name|NamedList
argument_list|<
name|Number
argument_list|>
name|stats
init|=
name|metrics
operator|.
name|getStatistics
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Buffer allocation metric not updating correctly."
argument_list|,
name|allocated
argument_list|,
name|isMetricPositive
argument_list|(
name|stats
argument_list|,
literal|"buffercache.allocations"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Buffer lost metric not updating correctly."
argument_list|,
name|lost
argument_list|,
name|isMetricPositive
argument_list|(
name|stats
argument_list|,
literal|"buffercache.lost"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|isMetricPositive
specifier|private
name|boolean
name|isMetricPositive
parameter_list|(
name|NamedList
argument_list|<
name|Number
argument_list|>
name|stats
parameter_list|,
name|String
name|metric
parameter_list|)
block|{
return|return
operator|new
name|BigDecimal
argument_list|(
name|stats
operator|.
name|get
argument_list|(
name|metric
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|compareTo
argument_list|(
name|BigDecimal
operator|.
name|ZERO
argument_list|)
operator|>
literal|0
return|;
block|}
block|}
end_class
end_unit