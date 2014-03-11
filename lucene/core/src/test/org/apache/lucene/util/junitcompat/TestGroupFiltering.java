begin_unit
begin_package
DECL|package|org.apache.lucene.util.junitcompat
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|junitcompat
package|;
end_package
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Documented
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Inherited
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Retention
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|RetentionPolicy
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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|TestGroup
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|TestGroupFiltering
specifier|public
class|class
name|TestGroupFiltering
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Documented
annotation|@
name|Inherited
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|TestGroup
argument_list|(
name|enabled
operator|=
literal|false
argument_list|)
DECL|interface|Foo
specifier|public
annotation_defn|@interface
name|Foo
block|{}
annotation|@
name|Documented
annotation|@
name|Inherited
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|TestGroup
argument_list|(
name|enabled
operator|=
literal|false
argument_list|)
DECL|interface|Bar
specifier|public
annotation_defn|@interface
name|Bar
block|{}
annotation|@
name|Documented
annotation|@
name|Inherited
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|TestGroup
argument_list|(
name|enabled
operator|=
literal|false
argument_list|)
DECL|interface|Jira
specifier|public
annotation_defn|@interface
name|Jira
block|{
DECL|method|bug
name|String
name|bug
parameter_list|()
function_decl|;
block|}
annotation|@
name|Foo
DECL|method|testFoo
specifier|public
name|void
name|testFoo
parameter_list|()
block|{}
annotation|@
name|Foo
annotation|@
name|Bar
DECL|method|testFooBar
specifier|public
name|void
name|testFooBar
parameter_list|()
block|{}
annotation|@
name|Bar
DECL|method|testBar
specifier|public
name|void
name|testBar
parameter_list|()
block|{}
annotation|@
name|Jira
argument_list|(
name|bug
operator|=
literal|"JIRA bug reference"
argument_list|)
DECL|method|testJira
specifier|public
name|void
name|testJira
parameter_list|()
block|{}
block|}
end_class
end_unit
