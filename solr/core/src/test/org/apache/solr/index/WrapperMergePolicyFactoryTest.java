begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.index
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|index
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
name|index
operator|.
name|MergePolicy
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
name|NoMergePolicy
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
name|TieredMergePolicy
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
name|UpgradeIndexMergePolicy
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
name|SolrTestCaseJ4
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
name|SolrResourceLoader
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
name|schema
operator|.
name|IndexSchema
import|;
end_import
begin_comment
comment|/** Unit tests for {@link WrapperMergePolicyFactory}. */
end_comment
begin_class
DECL|class|WrapperMergePolicyFactoryTest
specifier|public
class|class
name|WrapperMergePolicyFactoryTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|resourceLoader
specifier|private
specifier|final
name|SolrResourceLoader
name|resourceLoader
init|=
operator|new
name|SolrResourceLoader
argument_list|()
decl_stmt|;
DECL|method|testReturnsDefaultMergePolicyIfNoneSpecified
specifier|public
name|void
name|testReturnsDefaultMergePolicyIfNoneSpecified
parameter_list|()
block|{
specifier|final
name|MergePolicyFactoryArgs
name|args
init|=
operator|new
name|MergePolicyFactoryArgs
argument_list|()
decl_stmt|;
name|MergePolicyFactory
name|mpf
init|=
operator|new
name|DefaultingWrapperMergePolicyFactory
argument_list|(
name|resourceLoader
argument_list|,
name|args
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|mpf
operator|.
name|getMergePolicy
argument_list|()
argument_list|,
name|NoMergePolicy
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
DECL|method|testFailsIfNoClassSpecifiedForWrappedPolicy
specifier|public
name|void
name|testFailsIfNoClassSpecifiedForWrappedPolicy
parameter_list|()
block|{
specifier|final
name|MergePolicyFactoryArgs
name|args
init|=
operator|new
name|MergePolicyFactoryArgs
argument_list|()
decl_stmt|;
name|args
operator|.
name|add
argument_list|(
name|WrapperMergePolicyFactory
operator|.
name|WRAPPED_PREFIX
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|DefaultingWrapperMergePolicyFactory
argument_list|(
name|resourceLoader
argument_list|,
name|args
argument_list|,
literal|null
argument_list|)
operator|.
name|getMergePolicy
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have failed when no 'class' specified for wrapped merge policy"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// Good!
block|}
block|}
DECL|method|testProperlyInitializesWrappedMergePolicy
specifier|public
name|void
name|testProperlyInitializesWrappedMergePolicy
parameter_list|()
block|{
specifier|final
name|TieredMergePolicy
name|defaultTMP
init|=
operator|new
name|TieredMergePolicy
argument_list|()
decl_stmt|;
specifier|final
name|int
name|testMaxMergeAtOnce
init|=
name|defaultTMP
operator|.
name|getMaxMergeAtOnce
argument_list|()
operator|*
literal|2
decl_stmt|;
specifier|final
name|double
name|testMaxMergedSegmentMB
init|=
name|defaultTMP
operator|.
name|getMaxMergedSegmentMB
argument_list|()
operator|*
literal|10
decl_stmt|;
specifier|final
name|MergePolicyFactoryArgs
name|args
init|=
operator|new
name|MergePolicyFactoryArgs
argument_list|()
decl_stmt|;
name|args
operator|.
name|add
argument_list|(
name|WrapperMergePolicyFactory
operator|.
name|WRAPPED_PREFIX
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"test.class"
argument_list|,
name|TieredMergePolicyFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"test.maxMergeAtOnce"
argument_list|,
name|testMaxMergeAtOnce
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"test.maxMergedSegmentMB"
argument_list|,
name|testMaxMergedSegmentMB
argument_list|)
expr_stmt|;
name|MergePolicyFactory
name|mpf
init|=
operator|new
name|DefaultingWrapperMergePolicyFactory
argument_list|(
name|resourceLoader
argument_list|,
name|args
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|MergePolicy
name|getDefaultWrappedMergePolicy
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Should not have reached here!"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
specifier|final
name|MergePolicy
name|mp
init|=
name|mpf
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|mp
operator|.
name|getClass
argument_list|()
argument_list|,
name|TieredMergePolicy
operator|.
name|class
argument_list|)
expr_stmt|;
specifier|final
name|TieredMergePolicy
name|tmp
init|=
operator|(
name|TieredMergePolicy
operator|)
name|mp
decl_stmt|;
name|assertEquals
argument_list|(
literal|"maxMergeAtOnce"
argument_list|,
name|testMaxMergeAtOnce
argument_list|,
name|tmp
operator|.
name|getMaxMergeAtOnce
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"maxMergedSegmentMB"
argument_list|,
name|testMaxMergedSegmentMB
argument_list|,
name|tmp
operator|.
name|getMaxMergedSegmentMB
argument_list|()
argument_list|,
literal|0.0d
argument_list|)
expr_stmt|;
block|}
DECL|method|testUpgradeIndexMergePolicyFactory
specifier|public
name|void
name|testUpgradeIndexMergePolicyFactory
parameter_list|()
block|{
specifier|final
name|int
name|N
init|=
literal|10
decl_stmt|;
specifier|final
name|Double
name|wrappingNoCFSRatio
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|null
else|:
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|N
operator|+
literal|1
argument_list|)
operator|/
operator|(
operator|(
name|double
operator|)
name|N
operator|)
decl_stmt|;
comment|// must be: 0.0<= value<= 1.0
specifier|final
name|Double
name|wrappedNoCFSRatio
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|null
else|:
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|N
operator|+
literal|1
argument_list|)
operator|/
operator|(
operator|(
name|double
operator|)
name|N
operator|)
decl_stmt|;
comment|// must be: 0.0<= value<= 1.0
name|implTestUpgradeIndexMergePolicyFactory
argument_list|(
name|wrappingNoCFSRatio
argument_list|,
name|wrappedNoCFSRatio
argument_list|)
expr_stmt|;
block|}
DECL|method|implTestUpgradeIndexMergePolicyFactory
specifier|private
name|void
name|implTestUpgradeIndexMergePolicyFactory
parameter_list|(
name|Double
name|wrappingNoCFSRatio
parameter_list|,
name|Double
name|wrappedNoCFSRatio
parameter_list|)
block|{
specifier|final
name|MergePolicyFactoryArgs
name|args
init|=
operator|new
name|MergePolicyFactoryArgs
argument_list|()
decl_stmt|;
if|if
condition|(
name|wrappingNoCFSRatio
operator|!=
literal|null
condition|)
block|{
name|args
operator|.
name|add
argument_list|(
literal|"noCFSRatio"
argument_list|,
name|wrappingNoCFSRatio
argument_list|)
expr_stmt|;
comment|// noCFSRatio for the wrapping merge policy
block|}
name|args
operator|.
name|add
argument_list|(
name|WrapperMergePolicyFactory
operator|.
name|WRAPPED_PREFIX
argument_list|,
literal|"wrapped"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"wrapped.class"
argument_list|,
name|TieredMergePolicyFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|wrappedNoCFSRatio
operator|!=
literal|null
condition|)
block|{
name|args
operator|.
name|add
argument_list|(
literal|"wrapped.noCFSRatio"
argument_list|,
name|wrappedNoCFSRatio
argument_list|)
expr_stmt|;
comment|// noCFSRatio for the wrapped merge policy
block|}
name|MergePolicyFactory
name|mpf
decl_stmt|;
try|try
block|{
name|mpf
operator|=
operator|new
name|UpgradeIndexMergePolicyFactory
argument_list|(
name|resourceLoader
argument_list|,
name|args
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Should only reach here if wrapping and wrapped args don't overlap!"
argument_list|,
operator|(
name|wrappingNoCFSRatio
operator|!=
literal|null
operator|&&
name|wrappedNoCFSRatio
operator|!=
literal|null
operator|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|1
init|;
name|ii
operator|<=
literal|2
condition|;
operator|++
name|ii
control|)
block|{
comment|// it should be okay to call getMergePolicy() more than once
specifier|final
name|MergePolicy
name|mp
init|=
name|mpf
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
if|if
condition|(
name|wrappingNoCFSRatio
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
literal|"#"
operator|+
name|ii
operator|+
literal|" wrappingNoCFSRatio"
argument_list|,
name|wrappingNoCFSRatio
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|mp
operator|.
name|getNoCFSRatio
argument_list|()
argument_list|,
literal|0.0d
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|wrappedNoCFSRatio
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
literal|"#"
operator|+
name|ii
operator|+
literal|" wrappedNoCFSRatio"
argument_list|,
name|wrappedNoCFSRatio
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|mp
operator|.
name|getNoCFSRatio
argument_list|()
argument_list|,
literal|0.0d
argument_list|)
expr_stmt|;
block|}
name|assertSame
argument_list|(
name|mp
operator|.
name|getClass
argument_list|()
argument_list|,
name|UpgradeIndexMergePolicy
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Wrapping and wrapped merge policy args overlap! [noCFSRatio]"
argument_list|,
name|iae
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should only reach here if wrapping and wrapped args do overlap!"
argument_list|,
operator|(
name|wrappingNoCFSRatio
operator|!=
literal|null
operator|&&
name|wrappedNoCFSRatio
operator|!=
literal|null
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|DefaultingWrapperMergePolicyFactory
specifier|private
specifier|static
class|class
name|DefaultingWrapperMergePolicyFactory
extends|extends
name|WrapperMergePolicyFactory
block|{
DECL|method|DefaultingWrapperMergePolicyFactory
name|DefaultingWrapperMergePolicyFactory
parameter_list|(
name|SolrResourceLoader
name|resourceLoader
parameter_list|,
name|MergePolicyFactoryArgs
name|wrapperArgs
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
name|super
argument_list|(
name|resourceLoader
argument_list|,
name|wrapperArgs
argument_list|,
name|schema
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|args
operator|.
name|keys
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"All arguments should have been claimed by the wrapped policy but some ("
operator|+
name|args
operator|+
literal|") remain."
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDefaultWrappedMergePolicy
specifier|protected
name|MergePolicy
name|getDefaultWrappedMergePolicy
parameter_list|()
block|{
return|return
name|NoMergePolicy
operator|.
name|INSTANCE
return|;
block|}
annotation|@
name|Override
DECL|method|getMergePolicyInstance
specifier|protected
name|MergePolicy
name|getMergePolicyInstance
parameter_list|(
name|MergePolicy
name|wrappedMP
parameter_list|)
block|{
return|return
name|getWrappedMergePolicy
argument_list|()
return|;
block|}
block|}
block|}
end_class
end_unit