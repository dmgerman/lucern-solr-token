begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
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
name|InvocationTargetException
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
name|util
operator|.
name|Map
import|;
end_import
begin_comment
comment|/**  *<p>  * A Transformer instance capable of executing functions written in scripting  * languages as a Transformer instance.  *</p>  *<p/>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<p/>  *<b>This API is experimental and may change in the future.</b>  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|ScriptTransformer
specifier|public
class|class
name|ScriptTransformer
extends|extends
name|Transformer
block|{
DECL|field|engine
specifier|private
name|Object
name|engine
decl_stmt|;
DECL|field|invokeFunctionMethod
specifier|private
name|Method
name|invokeFunctionMethod
decl_stmt|;
DECL|field|functionName
specifier|private
name|String
name|functionName
decl_stmt|;
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|engine
operator|==
literal|null
condition|)
name|initEngine
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|engine
operator|==
literal|null
condition|)
return|return
name|row
return|;
return|return
name|invokeFunctionMethod
operator|.
name|invoke
argument_list|(
name|engine
argument_list|,
name|functionName
argument_list|,
operator|new
name|Object
index|[]
block|{
name|row
block|,
name|context
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|DataImportHandlerException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Could not invoke method :"
operator|+
name|functionName
operator|+
literal|"\n<script>\n"
operator|+
name|context
operator|.
name|getVariableResolver
argument_list|()
operator|.
name|resolve
argument_list|(
name|DataConfig
operator|.
name|IMPORTER_NS
operator|+
literal|"."
operator|+
name|DataConfig
operator|.
name|SCRIPT
argument_list|)
operator|+
literal|"</script>"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Error invoking script for entity "
operator|+
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|initEngine
specifier|private
name|void
name|initEngine
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
try|try
block|{
name|String
name|scriptText
init|=
name|context
operator|.
name|getScript
argument_list|()
decl_stmt|;
name|String
name|scriptLang
init|=
name|context
operator|.
name|getScriptLanguage
argument_list|()
decl_stmt|;
name|Object
name|scriptEngineMgr
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"javax.script.ScriptEngineManager"
argument_list|)
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|// create a Script engine
name|Method
name|getEngineMethod
init|=
name|scriptEngineMgr
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"getEngineByName"
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|engine
operator|=
name|getEngineMethod
operator|.
name|invoke
argument_list|(
name|scriptEngineMgr
argument_list|,
name|scriptLang
argument_list|)
expr_stmt|;
name|Method
name|evalMethod
init|=
name|engine
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"eval"
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|invokeFunctionMethod
operator|=
name|engine
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"invokeFunction"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|Object
index|[]
operator|.
expr|class
argument_list|)
expr_stmt|;
name|evalMethod
operator|.
name|invoke
argument_list|(
name|engine
argument_list|,
name|scriptText
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"<script> can be used only in java 6 or above"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|setFunctionName
specifier|public
name|void
name|setFunctionName
parameter_list|(
name|String
name|methodName
parameter_list|)
block|{
name|this
operator|.
name|functionName
operator|=
name|methodName
expr_stmt|;
block|}
DECL|method|getFunctionName
specifier|public
name|String
name|getFunctionName
parameter_list|()
block|{
return|return
name|functionName
return|;
block|}
block|}
end_class
end_unit
