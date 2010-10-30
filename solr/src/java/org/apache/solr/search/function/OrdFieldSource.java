begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
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
name|IndexReader
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
name|Map
import|;
end_import
begin_comment
comment|/**  * Obtains the ordinal of the field value from the default Lucene {@link org.apache.lucene.search.FieldCache} using getStringIndex().  *<br>  * The native lucene index order is used to assign an ordinal value for each field value.  *<br>Field values (terms) are lexicographically ordered by unicode value, and numbered starting at 1.  *<br>  * Example:<br>  *  If there were only three field values: "apple","banana","pear"  *<br>then ord("apple")=1, ord("banana")=2, ord("pear")=3  *<p>  * WARNING: ord() depends on the position in an index and can thus change when other documents are inserted or deleted,  *  or if a MultiSearcher is used.  *<br>WARNING: as of Solr 1.4, ord() and rord() can cause excess memory use since they must use a FieldCache entry  * at the top level reader, while sorting and function queries now use entries at the segment level.  Hence sorting  * or using a different function query, in addition to ord()/rord() will double memory use.  * @version $Id$  */
end_comment
begin_class
DECL|class|OrdFieldSource
specifier|public
class|class
name|OrdFieldSource
extends|extends
name|ValueSource
block|{
DECL|field|field
specifier|protected
name|String
name|field
decl_stmt|;
DECL|method|OrdFieldSource
specifier|public
name|OrdFieldSource
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"ord("
operator|+
name|field
operator|+
literal|')'
return|;
block|}
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|StringIndexDocValues
argument_list|(
name|this
argument_list|,
name|reader
argument_list|,
name|field
argument_list|)
block|{
specifier|protected
name|String
name|toTerm
parameter_list|(
name|String
name|readableValue
parameter_list|)
block|{
return|return
name|readableValue
return|;
block|}
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|termsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
return|;
block|}
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|termsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
return|;
block|}
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|termsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
return|;
block|}
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|double
operator|)
name|termsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
return|;
block|}
specifier|public
name|int
name|ordVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|termsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
return|;
block|}
specifier|public
name|int
name|numOrd
parameter_list|()
block|{
return|return
name|termsIndex
operator|.
name|numOrd
argument_list|()
return|;
block|}
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
comment|// the string value of the ordinal, not the string itself
return|return
name|Integer
operator|.
name|toString
argument_list|(
name|termsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|description
argument_list|()
operator|+
literal|'='
operator|+
name|intVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|.
name|getClass
argument_list|()
operator|==
name|OrdFieldSource
operator|.
name|class
operator|&&
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|OrdFieldSource
operator|)
name|o
operator|)
operator|.
name|field
argument_list|)
return|;
block|}
DECL|field|hcode
specifier|private
specifier|static
specifier|final
name|int
name|hcode
init|=
name|OrdFieldSource
operator|.
name|class
operator|.
name|hashCode
argument_list|()
decl_stmt|;
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hcode
operator|+
name|field
operator|.
name|hashCode
argument_list|()
return|;
block|}
empty_stmt|;
block|}
end_class
end_unit
