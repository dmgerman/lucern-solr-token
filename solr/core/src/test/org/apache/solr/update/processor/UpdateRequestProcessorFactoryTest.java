begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
operator|.
name|DistributingUpdateProcessorFactory
operator|.
name|DISTRIB_UPDATE_PARAM
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|solr
operator|.
name|core
operator|.
name|SolrCore
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
name|response
operator|.
name|SolrQueryResponse
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
name|util
operator|.
name|AbstractSolrTestCase
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
begin_comment
comment|/**  *   */
end_comment
begin_class
DECL|class|UpdateRequestProcessorFactoryTest
specifier|public
class|class
name|UpdateRequestProcessorFactoryTest
extends|extends
name|AbstractSolrTestCase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-transformers.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|testConfiguration
specifier|public
name|void
name|testConfiguration
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
comment|// make sure it loaded the factories
name|UpdateRequestProcessorChain
name|chained
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|"standard"
argument_list|)
decl_stmt|;
comment|// Make sure it got 3 items (4 configured, 1 is enable=false)
name|assertEquals
argument_list|(
literal|"wrong number of (enabled) factories in chain"
argument_list|,
literal|3
argument_list|,
name|chained
operator|.
name|getFactories
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// first one should be log, and it should be configured properly
name|UpdateRequestProcessorFactory
name|first
init|=
name|chained
operator|.
name|getFactories
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong factory at front of chain"
argument_list|,
name|LogUpdateProcessorFactory
operator|.
name|class
argument_list|,
name|first
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|LogUpdateProcessorFactory
name|log
init|=
operator|(
name|LogUpdateProcessorFactory
operator|)
name|first
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong config for LogUpdateProcessorFactory.maxNumToLog"
argument_list|,
literal|100
argument_list|,
name|log
operator|.
name|maxNumToLog
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong config for LogUpdateProcessorFactory.slowUpdateThresholdMillis"
argument_list|,
literal|2000
argument_list|,
name|log
operator|.
name|slowUpdateThresholdMillis
argument_list|)
expr_stmt|;
name|UpdateRequestProcessorChain
name|custom
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|CustomUpdateRequestProcessorFactory
name|link
init|=
operator|(
name|CustomUpdateRequestProcessorFactory
operator|)
name|custom
operator|.
name|getFactories
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|custom
argument_list|,
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|custom
argument_list|,
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|"custom"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure the NamedListArgs got through ok
name|assertEquals
argument_list|(
literal|"{name={n8=88,n9=99}}"
argument_list|,
name|link
operator|.
name|args
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testUpdateDistribChainSkipping
specifier|public
name|void
name|testUpdateDistribChainSkipping
parameter_list|()
throws|throws
name|Exception
block|{
comment|// a key part of this test is verifying that LogUpdateProcessor is found in all chains because it
comment|// is a @RunAllways processor -- but in order for that to work, we have to sanity check that the log
comment|// level is at least "INFO" otherwise the factory won't even produce a processor and all our assertions
comment|// are for nought.  (see LogUpdateProcessorFactory.getInstance)
comment|//
comment|// TODO: maybe create a new mock Processor w/ @RunAlways annot if folks feel requiring INFO is evil.
name|assertTrue
argument_list|(
literal|"Tests must be run with INFO level logging "
operator|+
literal|"otherwise LogUpdateProcessor isn't used and can't be tested."
argument_list|,
name|LogUpdateProcessor
operator|.
name|log
operator|.
name|isInfoEnabled
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|EXPECTED_CHAIN_LENGTH
init|=
literal|5
decl_stmt|;
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|name
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"distrib-chain-explicit"
argument_list|,
literal|"distrib-chain-implicit"
argument_list|,
literal|"distrib-chain-noop"
argument_list|)
control|)
block|{
name|UpdateRequestProcessor
name|proc
decl_stmt|;
name|List
argument_list|<
name|UpdateRequestProcessor
argument_list|>
name|procs
decl_stmt|;
name|UpdateRequestProcessorChain
name|chain
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|name
argument_list|,
name|chain
argument_list|)
expr_stmt|;
comment|// either explicitly, or because of injection
name|assertEquals
argument_list|(
name|name
operator|+
literal|" chain length: "
operator|+
name|chain
operator|.
name|toString
argument_list|()
argument_list|,
name|EXPECTED_CHAIN_LENGTH
argument_list|,
name|chain
operator|.
name|getFactories
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// test a basic (non distrib) chain
name|proc
operator|=
name|chain
operator|.
name|createProcessor
argument_list|(
name|req
argument_list|()
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|)
expr_stmt|;
name|procs
operator|=
name|procToList
argument_list|(
name|proc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|name
operator|+
literal|" procs size: "
operator|+
name|procs
operator|.
name|toString
argument_list|()
argument_list|,
comment|// -1 = NoOpDistributingUpdateProcessorFactory produces no processor
name|EXPECTED_CHAIN_LENGTH
operator|-
operator|(
literal|"distrib-chain-noop"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|?
literal|1
else|:
literal|0
operator|)
argument_list|,
name|procs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Custom comes first in all three of our chains
name|assertTrue
argument_list|(
name|name
operator|+
literal|" first processor isn't a CustomUpdateRequestProcessor: "
operator|+
name|procs
operator|.
name|toString
argument_list|()
argument_list|,
operator|(
comment|// compare them both just because i'm going insane and the more checks the better
name|proc
operator|instanceof
name|CustomUpdateRequestProcessor
operator|&&
name|procs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|CustomUpdateRequestProcessor
operator|)
argument_list|)
expr_stmt|;
comment|// Log should always come second in our chain.
name|assertNotNull
argument_list|(
name|name
operator|+
literal|" proc.next is null"
argument_list|,
name|proc
operator|.
name|next
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|name
operator|+
literal|" second proc is null"
argument_list|,
name|procs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|name
operator|+
literal|" second proc isn't LogUpdateProcessor: "
operator|+
name|procs
operator|.
name|toString
argument_list|()
argument_list|,
operator|(
comment|// compare them both just because i'm going insane and the more checks the better
name|proc
operator|.
name|next
operator|instanceof
name|LogUpdateProcessor
operator|&&
name|procs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|instanceof
name|LogUpdateProcessor
operator|)
argument_list|)
expr_stmt|;
comment|// fetch the distributed version of this chain
name|proc
operator|=
name|chain
operator|.
name|createProcessor
argument_list|(
name|req
argument_list|(
name|DISTRIB_UPDATE_PARAM
argument_list|,
literal|"non_blank_value"
argument_list|)
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|)
expr_stmt|;
name|procs
operator|=
name|procToList
argument_list|(
name|proc
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|name
operator|+
literal|" (distrib) chain produced null proc"
argument_list|,
name|proc
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|name
operator|+
literal|" (distrib) procs is empty"
argument_list|,
name|procs
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// for these 3 (distrib) chains, the first proc should always be LogUpdateProcessor
name|assertTrue
argument_list|(
name|name
operator|+
literal|" (distrib) first proc should be LogUpdateProcessor because of @RunAllways: "
operator|+
name|procs
operator|.
name|toString
argument_list|()
argument_list|,
operator|(
comment|// compare them both just because i'm going insane and the more checks the better
name|proc
operator|instanceof
name|LogUpdateProcessor
operator|&&
name|procs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|LogUpdateProcessor
operator|)
argument_list|)
expr_stmt|;
comment|// for these 3 (distrib) chains, the last proc should always be RunUpdateProcessor
name|assertTrue
argument_list|(
name|name
operator|+
literal|" (distrib) last processor isn't a RunUpdateProcessor: "
operator|+
name|procs
operator|.
name|toString
argument_list|()
argument_list|,
name|procs
operator|.
name|get
argument_list|(
name|procs
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|instanceof
name|RunUpdateProcessor
argument_list|)
expr_stmt|;
comment|// either 1 proc was droped in distrib mode, or 1 for the "implicit" chain
name|assertEquals
argument_list|(
name|name
operator|+
literal|" (distrib) chain has wrong length: "
operator|+
name|procs
operator|.
name|toString
argument_list|()
argument_list|,
comment|// -1 = all chains lose CustomUpdateRequestProcessorFactory
comment|// -1 = distrib-chain-noop: NoOpDistributingUpdateProcessorFactory produces no processor
comment|// -1 = distrib-chain-implicit: does RemoveBlank before distrib
name|EXPECTED_CHAIN_LENGTH
operator|-
operator|(
literal|"distrib-chain-explicit"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|?
literal|1
else|:
literal|2
operator|)
argument_list|,
name|procs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * walks the "next" values of the proc building up a List of the procs for easier testing    */
DECL|method|procToList
specifier|public
specifier|static
name|List
argument_list|<
name|UpdateRequestProcessor
argument_list|>
name|procToList
parameter_list|(
name|UpdateRequestProcessor
name|proc
parameter_list|)
block|{
name|List
argument_list|<
name|UpdateRequestProcessor
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|UpdateRequestProcessor
argument_list|>
argument_list|(
literal|7
argument_list|)
decl_stmt|;
while|while
condition|(
literal|null
operator|!=
name|proc
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|proc
argument_list|)
expr_stmt|;
name|proc
operator|=
name|proc
operator|.
name|next
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
