begin_unit
begin_package
DECL|package|org.apache.solr.common
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|EntityResolver
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLInputFactory
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLResolver
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|input
operator|.
name|ClosedInputStream
import|;
end_import
begin_comment
comment|/**  * This class provides several singletons of entity resolvers used by  * SAX and StAX in the Java API. This is needed to make secure  * XML parsers, that don't resolve external entities from untrusted sources.  *<p>This class also provides static methods to configure SAX and StAX  * parsers to be safe.  *<p>Parsers will get an empty, closed stream for every external  * entity, so they will not fail while parsing (unless the external entity  * is needed for processing!).  */
end_comment
begin_class
DECL|class|EmptyEntityResolver
specifier|public
specifier|final
class|class
name|EmptyEntityResolver
block|{
DECL|field|SAX_INSTANCE
specifier|public
specifier|static
specifier|final
name|EntityResolver
name|SAX_INSTANCE
init|=
operator|new
name|EntityResolver
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InputSource
name|resolveEntity
parameter_list|(
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|)
block|{
return|return
operator|new
name|InputSource
argument_list|(
name|ClosedInputStream
operator|.
name|CLOSED_INPUT_STREAM
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|STAX_INSTANCE
specifier|public
specifier|static
specifier|final
name|XMLResolver
name|STAX_INSTANCE
init|=
operator|new
name|XMLResolver
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InputStream
name|resolveEntity
parameter_list|(
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|,
name|String
name|baseURI
parameter_list|,
name|String
name|namespace
parameter_list|)
block|{
return|return
name|ClosedInputStream
operator|.
name|CLOSED_INPUT_STREAM
return|;
block|}
block|}
decl_stmt|;
comment|// no instance!
DECL|method|EmptyEntityResolver
specifier|private
name|EmptyEntityResolver
parameter_list|()
block|{}
DECL|method|trySetSAXFeature
specifier|private
specifier|static
name|void
name|trySetSAXFeature
parameter_list|(
name|SAXParserFactory
name|saxFactory
parameter_list|,
name|String
name|feature
parameter_list|,
name|boolean
name|enabled
parameter_list|)
block|{
try|try
block|{
name|saxFactory
operator|.
name|setFeature
argument_list|(
name|feature
argument_list|,
name|enabled
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|// ignore
block|}
block|}
comment|/** Configures the given {@link SAXParserFactory} to do secure XML processing of untrusted sources.    * It is required to also set {@link #SAX_INSTANCE} on the created {@link org.xml.sax.XMLReader}.    * @see #SAX_INSTANCE    */
DECL|method|configureSAXParserFactory
specifier|public
specifier|static
name|void
name|configureSAXParserFactory
parameter_list|(
name|SAXParserFactory
name|saxFactory
parameter_list|)
block|{
comment|// don't enable validation of DTDs:
name|saxFactory
operator|.
name|setValidating
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// enable secure processing:
name|trySetSAXFeature
argument_list|(
name|saxFactory
argument_list|,
name|XMLConstants
operator|.
name|FEATURE_SECURE_PROCESSING
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|trySetStAXProperty
specifier|private
specifier|static
name|void
name|trySetStAXProperty
parameter_list|(
name|XMLInputFactory
name|inputFactory
parameter_list|,
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
try|try
block|{
name|inputFactory
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|// ignore
block|}
block|}
comment|/** Configures the given {@link XMLInputFactory} to not parse external entities.    * No further configuration on is needed, all required entity resolvers are configured.    */
DECL|method|configureXMLInputFactory
specifier|public
specifier|static
name|void
name|configureXMLInputFactory
parameter_list|(
name|XMLInputFactory
name|inputFactory
parameter_list|)
block|{
comment|// don't enable validation of DTDs:
name|trySetStAXProperty
argument_list|(
name|inputFactory
argument_list|,
name|XMLInputFactory
operator|.
name|IS_VALIDATING
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
comment|// enable this to *not* produce parsing failure on external entities:
name|trySetStAXProperty
argument_list|(
name|inputFactory
argument_list|,
name|XMLInputFactory
operator|.
name|IS_SUPPORTING_EXTERNAL_ENTITIES
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|inputFactory
operator|.
name|setXMLResolver
argument_list|(
name|EmptyEntityResolver
operator|.
name|STAX_INSTANCE
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit