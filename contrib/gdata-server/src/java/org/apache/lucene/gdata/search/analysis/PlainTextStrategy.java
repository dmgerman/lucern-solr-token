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
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|data
operator|.
name|ServerBaseEntry
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
name|gdata
operator|.
name|search
operator|.
name|config
operator|.
name|IndexSchemaField
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
comment|/**  * @author Simon Willnauer  *   */
end_comment
begin_class
DECL|class|PlainTextStrategy
specifier|public
class|class
name|PlainTextStrategy
extends|extends
name|ContentStrategy
block|{
DECL|method|PlainTextStrategy
specifier|protected
name|PlainTextStrategy
parameter_list|(
name|IndexSchemaField
name|fieldConfiguration
parameter_list|)
block|{
name|super
argument_list|(
name|fieldConfiguration
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.apache.lucene.gdata.search.analysis.ContentStrategy#processIndexable(org.apache.lucene.gdata.search.analysis.Indexable)      */
annotation|@
name|Override
DECL|method|processIndexable
specifier|public
name|void
name|processIndexable
parameter_list|(
name|Indexable
argument_list|<
name|?
extends|extends
name|Node
argument_list|,
name|?
extends|extends
name|ServerBaseEntry
argument_list|>
name|indexable
parameter_list|)
throws|throws
name|NotIndexableException
block|{
name|String
name|path
init|=
name|this
operator|.
name|config
operator|.
name|getPath
argument_list|()
decl_stmt|;
try|try
block|{
name|Node
name|node
init|=
name|indexable
operator|.
name|applyPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NotIndexableException
argument_list|(
literal|"Could not retrieve content for schema field: "
operator|+
name|this
operator|.
name|config
argument_list|)
throw|;
name|this
operator|.
name|content
operator|=
name|node
operator|.
name|getTextContent
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathExpressionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NotIndexableException
argument_list|(
literal|"Can not apply Path"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
