begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package
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
name|document
operator|.
name|Fieldable
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
name|IndexReader
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
name|search
operator|.
name|ScoreDoc
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
name|search
operator|.
name|ScoreDocComparator
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
name|search
operator|.
name|SortComparatorSource
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
name|search
operator|.
name|SortField
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
name|request
operator|.
name|TextResponseWriter
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
name|request
operator|.
name|XMLWriter
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
name|search
operator|.
name|function
operator|.
name|ValueSource
import|;
end_import
begin_comment
comment|/**  * Utility Field used for random sorting.  It should not be passed a value.  *   * To enable random sorting, you will need to add something like this   * to the schema.xml  *   *<types>  *  ...  *<fieldType name="random" class="solr.RandomSortField" />  *  ...   *</types>  *<fields>  *  ...  *<field name="random" type="random" indexed="true" stored="false"/>  *  ...  *</fields>  *    * @author ryan  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|RandomSortField
specifier|public
class|class
name|RandomSortField
extends|extends
name|FieldType
block|{
comment|/** Special comparator for sorting hits in random order */
DECL|field|COMPARE
specifier|private
specifier|static
specifier|final
name|ScoreDocComparator
name|COMPARE
init|=
operator|new
name|ScoreDocComparator
argument_list|()
block|{
specifier|final
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|public
name|int
name|compare
parameter_list|(
name|ScoreDoc
name|i
parameter_list|,
name|ScoreDoc
name|j
parameter_list|)
block|{
return|return
operator|(
name|rand
operator|.
name|nextInt
argument_list|()
operator|&
literal|0x2
operator|)
operator|-
literal|1
return|;
comment|// (rand.nextBoolean()) ? 1 : -1;
block|}
specifier|public
name|Comparable
name|sortValue
parameter_list|(
name|ScoreDoc
name|i
parameter_list|)
block|{
return|return
operator|new
name|Float
argument_list|(
name|rand
operator|.
name|nextFloat
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|int
name|sortType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|CUSTOM
return|;
block|}
block|}
decl_stmt|;
comment|/** use random sorting order.  */
DECL|class|RandomSort
specifier|private
specifier|static
class|class
name|RandomSort
extends|extends
name|SortField
block|{
DECL|method|RandomSort
specifier|public
name|RandomSort
parameter_list|(
name|String
name|n
parameter_list|)
block|{
name|super
argument_list|(
name|n
argument_list|,
name|SortField
operator|.
name|CUSTOM
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFactory
specifier|public
name|SortComparatorSource
name|getFactory
parameter_list|()
block|{
return|return
operator|new
name|SortComparatorSource
argument_list|()
block|{
specifier|public
name|ScoreDocComparator
name|newComparator
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|fieldname
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|COMPARE
return|;
block|}
block|}
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
return|return
operator|new
name|RandomSort
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|(
name|SchemaField
name|field
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Random field does not have a value source"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|XMLWriter
name|xmlWriter
parameter_list|,
name|String
name|name
parameter_list|,
name|Fieldable
name|f
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|TextResponseWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|Fieldable
name|f
parameter_list|)
block|{}
block|}
end_class
end_unit
