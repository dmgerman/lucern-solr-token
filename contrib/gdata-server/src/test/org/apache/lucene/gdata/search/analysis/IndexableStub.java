begin_unit
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.search.analysis
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
name|analysis
package|;
end_package
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationHandler
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Proxy
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NamedNodeMap
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import
begin_comment
comment|/**  * @author Simon Willnauer  *  */
end_comment
begin_class
DECL|class|IndexableStub
specifier|public
class|class
name|IndexableStub
extends|extends
name|Indexable
block|{
DECL|field|content
specifier|private
name|String
name|content
decl_stmt|;
DECL|field|retNull
specifier|private
name|boolean
name|retNull
decl_stmt|;
DECL|field|times
name|int
name|times
init|=
literal|1
decl_stmt|;
DECL|field|count
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|method|IndexableStub
name|IndexableStub
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|returnProxyTimes
specifier|public
name|void
name|returnProxyTimes
parameter_list|(
name|int
name|times
parameter_list|)
block|{
name|this
operator|.
name|times
operator|=
name|times
expr_stmt|;
block|}
DECL|method|setReturnNull
specifier|public
name|void
name|setReturnNull
parameter_list|(
name|boolean
name|returnNull
parameter_list|)
block|{
name|this
operator|.
name|retNull
operator|=
name|returnNull
expr_stmt|;
block|}
DECL|method|setReturnValueTextContent
specifier|public
name|void
name|setReturnValueTextContent
parameter_list|(
name|String
name|content
parameter_list|)
block|{
name|this
operator|.
name|content
operator|=
name|content
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|applyPath
specifier|public
name|Node
name|applyPath
parameter_list|(
name|String
name|xPath
parameter_list|)
throws|throws
name|XPathExpressionException
block|{
if|if
condition|(
name|xPath
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathExpressionException
argument_list|(
literal|"path is null"
argument_list|)
throw|;
if|if
condition|(
name|this
operator|.
name|retNull
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|times
operator|==
name|count
condition|)
return|return
literal|null
return|;
name|times
operator|++
expr_stmt|;
return|return
operator|(
name|Node
operator|)
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
index|[]
block|{
name|Node
operator|.
name|class
block|,
name|NamedNodeMap
operator|.
name|class
block|}
argument_list|,
operator|new
name|Handler
argument_list|(
name|this
operator|.
name|content
argument_list|)
argument_list|)
return|;
block|}
DECL|class|Handler
specifier|private
specifier|static
class|class
name|Handler
implements|implements
name|InvocationHandler
block|{
DECL|field|returnValue
name|String
name|returnValue
decl_stmt|;
DECL|method|Handler
specifier|public
name|Handler
parameter_list|(
name|String
name|toReturn
parameter_list|)
block|{
name|this
operator|.
name|returnValue
operator|=
name|toReturn
expr_stmt|;
block|}
DECL|method|invoke
specifier|public
name|Object
name|invoke
parameter_list|(
name|Object
name|proxy
parameter_list|,
name|Method
name|method
parameter_list|,
name|Object
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
if|if
condition|(
name|method
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"getNextSibling"
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|method
operator|.
name|getReturnType
argument_list|()
operator|==
name|String
operator|.
name|class
condition|)
return|return
name|this
operator|.
name|returnValue
return|;
if|if
condition|(
name|method
operator|.
name|getReturnType
argument_list|()
operator|==
name|Node
operator|.
name|class
condition|)
return|return
operator|(
name|Node
operator|)
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
index|[]
block|{
name|Node
operator|.
name|class
block|,
name|NamedNodeMap
operator|.
name|class
block|}
argument_list|,
operator|new
name|Handler
argument_list|(
name|this
operator|.
name|returnValue
argument_list|)
argument_list|)
return|;
if|if
condition|(
name|method
operator|.
name|getReturnType
argument_list|()
operator|==
name|NamedNodeMap
operator|.
name|class
condition|)
return|return
operator|(
name|NamedNodeMap
operator|)
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
index|[]
block|{
name|Node
operator|.
name|class
block|,
name|NamedNodeMap
operator|.
name|class
block|}
argument_list|,
operator|new
name|Handler
argument_list|(
name|this
operator|.
name|returnValue
argument_list|)
argument_list|)
return|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class
end_unit
