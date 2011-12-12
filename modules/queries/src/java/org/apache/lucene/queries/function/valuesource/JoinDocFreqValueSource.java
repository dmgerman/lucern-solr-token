begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.queries.function.valuesource
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
operator|.
name|valuesource
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
name|Map
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
name|index
operator|.
name|IndexReader
operator|.
name|AtomicReaderContext
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|IntDocValues
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
name|FieldCache
operator|.
name|DocTerms
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
name|BytesRef
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
name|ReaderUtil
import|;
end_import
begin_comment
comment|/**  * Use a field value and find the Document Frequency within another field.  *   * @since solr 4.0  */
end_comment
begin_class
DECL|class|JoinDocFreqValueSource
specifier|public
class|class
name|JoinDocFreqValueSource
extends|extends
name|FieldCacheSource
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"joindf"
decl_stmt|;
DECL|field|qfield
specifier|protected
specifier|final
name|String
name|qfield
decl_stmt|;
DECL|method|JoinDocFreqValueSource
specifier|public
name|JoinDocFreqValueSource
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|qfield
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|qfield
operator|=
name|qfield
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|NAME
operator|+
literal|"("
operator|+
name|field
operator|+
literal|":("
operator|+
name|qfield
operator|+
literal|"))"
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|FunctionValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocTerms
name|terms
init|=
name|cache
operator|.
name|getTerms
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|,
name|field
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|IndexReader
name|top
init|=
name|ReaderUtil
operator|.
name|getTopLevelContext
argument_list|(
name|readerContext
argument_list|)
operator|.
name|reader
decl_stmt|;
return|return
operator|new
name|IntDocValues
argument_list|(
name|this
argument_list|)
block|{
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
try|try
block|{
name|terms
operator|.
name|getTerm
argument_list|(
name|doc
argument_list|,
name|ref
argument_list|)
expr_stmt|;
name|int
name|v
init|=
name|top
operator|.
name|docFreq
argument_list|(
name|qfield
argument_list|,
name|ref
argument_list|)
decl_stmt|;
comment|//System.out.println( NAME+"["+field+"="+ref.utf8ToString()+"=("+qfield+":"+v+")]" );
return|return
name|v
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"caught exception in function "
operator|+
name|description
argument_list|()
operator|+
literal|" : doc="
operator|+
name|doc
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|JoinDocFreqValueSource
operator|.
name|class
condition|)
return|return
literal|false
return|;
name|JoinDocFreqValueSource
name|other
init|=
operator|(
name|JoinDocFreqValueSource
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|qfield
operator|.
name|equals
argument_list|(
name|other
operator|.
name|qfield
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|qfield
operator|.
name|hashCode
argument_list|()
operator|+
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
empty_stmt|;
block|}
end_class
end_unit
