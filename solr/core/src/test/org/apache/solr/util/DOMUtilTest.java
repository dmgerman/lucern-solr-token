begin_unit
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
import|;
end_import
begin_class
DECL|class|DOMUtilTest
specifier|public
class|class
name|DOMUtilTest
extends|extends
name|DOMUtilTestBase
block|{
DECL|method|testAddToNamedListPrimitiveTypes
specifier|public
name|void
name|testAddToNamedListPrimitiveTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|namedList
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|DOMUtil
operator|.
name|addToNamedList
argument_list|(
name|getNode
argument_list|(
literal|"<str name=\"String\">STRING</str>"
argument_list|,
literal|"/str"
argument_list|)
argument_list|,
name|namedList
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTypeAndValue
argument_list|(
name|namedList
argument_list|,
literal|"String"
argument_list|,
literal|"STRING"
argument_list|)
expr_stmt|;
name|DOMUtil
operator|.
name|addToNamedList
argument_list|(
name|getNode
argument_list|(
literal|"<int name=\"Integer\">100</int>"
argument_list|,
literal|"/int"
argument_list|)
argument_list|,
name|namedList
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTypeAndValue
argument_list|(
name|namedList
argument_list|,
literal|"Integer"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|DOMUtil
operator|.
name|addToNamedList
argument_list|(
name|getNode
argument_list|(
literal|"<long name=\"Long\">200</long>"
argument_list|,
literal|"/long"
argument_list|)
argument_list|,
name|namedList
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTypeAndValue
argument_list|(
name|namedList
argument_list|,
literal|"Long"
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|200
argument_list|)
argument_list|)
expr_stmt|;
name|DOMUtil
operator|.
name|addToNamedList
argument_list|(
name|getNode
argument_list|(
literal|"<float name=\"Float\">300</float>"
argument_list|,
literal|"/float"
argument_list|)
argument_list|,
name|namedList
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTypeAndValue
argument_list|(
name|namedList
argument_list|,
literal|"Float"
argument_list|,
name|Float
operator|.
name|valueOf
argument_list|(
literal|300
argument_list|)
argument_list|)
expr_stmt|;
name|DOMUtil
operator|.
name|addToNamedList
argument_list|(
name|getNode
argument_list|(
literal|"<double name=\"Double\">400</double>"
argument_list|,
literal|"/double"
argument_list|)
argument_list|,
name|namedList
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTypeAndValue
argument_list|(
name|namedList
argument_list|,
literal|"Double"
argument_list|,
name|Double
operator|.
name|valueOf
argument_list|(
literal|400
argument_list|)
argument_list|)
expr_stmt|;
name|DOMUtil
operator|.
name|addToNamedList
argument_list|(
name|getNode
argument_list|(
literal|"<bool name=\"Boolean\">true</bool>"
argument_list|,
literal|"/bool"
argument_list|)
argument_list|,
name|namedList
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTypeAndValue
argument_list|(
name|namedList
argument_list|,
literal|"Boolean"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|DOMUtil
operator|.
name|addToNamedList
argument_list|(
name|getNode
argument_list|(
literal|"<bool name=\"Boolean\">on</bool>"
argument_list|,
literal|"/bool"
argument_list|)
argument_list|,
name|namedList
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTypeAndValue
argument_list|(
name|namedList
argument_list|,
literal|"Boolean"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|DOMUtil
operator|.
name|addToNamedList
argument_list|(
name|getNode
argument_list|(
literal|"<bool name=\"Boolean\">yes</bool>"
argument_list|,
literal|"/bool"
argument_list|)
argument_list|,
name|namedList
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTypeAndValue
argument_list|(
name|namedList
argument_list|,
literal|"Boolean"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|DOMUtil
operator|.
name|addToNamedList
argument_list|(
name|getNode
argument_list|(
literal|"<bool name=\"Boolean\">false</bool>"
argument_list|,
literal|"/bool"
argument_list|)
argument_list|,
name|namedList
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTypeAndValue
argument_list|(
name|namedList
argument_list|,
literal|"Boolean"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|DOMUtil
operator|.
name|addToNamedList
argument_list|(
name|getNode
argument_list|(
literal|"<bool name=\"Boolean\">off</bool>"
argument_list|,
literal|"/bool"
argument_list|)
argument_list|,
name|namedList
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTypeAndValue
argument_list|(
name|namedList
argument_list|,
literal|"Boolean"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|DOMUtil
operator|.
name|addToNamedList
argument_list|(
name|getNode
argument_list|(
literal|"<bool name=\"Boolean\">no</bool>"
argument_list|,
literal|"/bool"
argument_list|)
argument_list|,
name|namedList
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTypeAndValue
argument_list|(
name|namedList
argument_list|,
literal|"Boolean"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|assertTypeAndValue
specifier|private
name|void
name|assertTypeAndValue
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|namedList
parameter_list|,
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|Object
name|v
init|=
name|namedList
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|key
argument_list|,
name|v
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|namedList
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
