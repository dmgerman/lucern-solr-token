begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
package|;
end_package
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
name|SolrDocumentList
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
name|SolrDocument
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
name|SolrInputDocument
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
name|params
operator|.
name|CommonParams
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
name|DocList
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
name|SolrIndexSearcher
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
name|DocIterator
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
name|FieldType
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
name|SchemaField
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
name|Document
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
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_comment
comment|/**  *   *   * This class serves as a basis from which {@link QueryResponseWriter}s can be  * developed. The class provides a single method  * {@link #write(SingleResponseWriter, SolrQueryRequest, SolrQueryResponse)}  * that allows users to implement a {@link SingleResponseWriter} sub-class which  * defines how to output {@link SolrInputDocument}s or a  * {@link SolrDocumentList}.  *   * @version $Id$  * @since 1.5  *   */
end_comment
begin_class
DECL|class|BaseResponseWriter
specifier|public
specifier|abstract
class|class
name|BaseResponseWriter
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BaseResponseWriter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SCORE_FIELD
specifier|private
specifier|static
specifier|final
name|String
name|SCORE_FIELD
init|=
literal|"score"
decl_stmt|;
comment|/**    *     * The main method that allows users to write {@link SingleResponseWriter}s    * and provide them as the initial parameter<code>responseWriter</code> to    * this method which defines how output should be generated.    *     * @param responseWriter    *          The user-provided {@link SingleResponseWriter} implementation.    * @param request    *          The provided {@link SolrQueryRequest}.    * @param response    *          The provided {@link SolrQueryResponse}.    * @throws IOException    *           If any error occurs.    */
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|SingleResponseWriter
name|responseWriter
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|responseWriter
operator|.
name|start
argument_list|()
expr_stmt|;
name|NamedList
name|nl
init|=
name|response
operator|.
name|getValues
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nl
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|nl
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Object
name|val
init|=
name|nl
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"responseHeader"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|Boolean
name|omitHeader
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|CommonParams
operator|.
name|OMIT_HEADER
argument_list|)
decl_stmt|;
if|if
condition|(
name|omitHeader
operator|==
literal|null
operator|||
operator|!
name|omitHeader
condition|)
name|responseWriter
operator|.
name|writeResponseHeader
argument_list|(
operator|(
name|NamedList
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|SolrDocumentList
condition|)
block|{
name|SolrDocumentList
name|list
init|=
operator|(
name|SolrDocumentList
operator|)
name|val
decl_stmt|;
name|DocListInfo
name|info
init|=
operator|new
name|DocListInfo
argument_list|(
name|list
operator|.
name|getNumFound
argument_list|()
argument_list|,
name|list
operator|.
name|getStart
argument_list|()
argument_list|,
name|list
operator|.
name|getMaxScore
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|responseWriter
operator|.
name|isStreamingDocs
argument_list|()
condition|)
block|{
name|responseWriter
operator|.
name|startDocumentList
argument_list|(
name|info
argument_list|)
expr_stmt|;
for|for
control|(
name|SolrDocument
name|solrDocument
range|:
name|list
control|)
name|responseWriter
operator|.
name|writeDoc
argument_list|(
name|solrDocument
argument_list|)
expr_stmt|;
name|responseWriter
operator|.
name|endDocumentList
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|responseWriter
operator|.
name|writeAllDocs
argument_list|(
name|info
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|DocList
condition|)
block|{
name|DocList
name|docList
init|=
operator|(
name|DocList
operator|)
name|val
decl_stmt|;
name|int
name|sz
init|=
name|docList
operator|.
name|size
argument_list|()
decl_stmt|;
name|IdxInfo
name|idxInfo
init|=
operator|new
name|IdxInfo
argument_list|(
name|request
operator|.
name|getSchema
argument_list|()
argument_list|,
name|request
operator|.
name|getSearcher
argument_list|()
argument_list|,
name|response
operator|.
name|getReturnFields
argument_list|()
argument_list|)
decl_stmt|;
name|DocListInfo
name|info
init|=
operator|new
name|DocListInfo
argument_list|(
name|docList
operator|.
name|matches
argument_list|()
argument_list|,
name|docList
operator|.
name|offset
argument_list|()
argument_list|,
name|docList
operator|.
name|maxScore
argument_list|()
argument_list|)
decl_stmt|;
name|DocIterator
name|iterator
init|=
name|docList
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|responseWriter
operator|.
name|isStreamingDocs
argument_list|()
condition|)
block|{
name|responseWriter
operator|.
name|startDocumentList
argument_list|(
name|info
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|sz
condition|;
name|j
operator|++
control|)
block|{
name|SolrDocument
name|sdoc
init|=
name|getDoc
argument_list|(
name|iterator
operator|.
name|nextDoc
argument_list|()
argument_list|,
name|idxInfo
argument_list|)
decl_stmt|;
if|if
condition|(
name|idxInfo
operator|.
name|includeScore
operator|&&
name|docList
operator|.
name|hasScores
argument_list|()
condition|)
block|{
name|sdoc
operator|.
name|addField
argument_list|(
name|SCORE_FIELD
argument_list|,
name|iterator
operator|.
name|score
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|responseWriter
operator|.
name|writeDoc
argument_list|(
name|sdoc
argument_list|)
expr_stmt|;
block|}
name|responseWriter
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|ArrayList
argument_list|<
name|SolrDocument
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|SolrDocument
argument_list|>
argument_list|(
name|docList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|sz
condition|;
name|j
operator|++
control|)
block|{
name|SolrDocument
name|sdoc
init|=
name|getDoc
argument_list|(
name|iterator
operator|.
name|nextDoc
argument_list|()
argument_list|,
name|idxInfo
argument_list|)
decl_stmt|;
if|if
condition|(
name|idxInfo
operator|.
name|includeScore
operator|&&
name|docList
operator|.
name|hasScores
argument_list|()
condition|)
block|{
name|sdoc
operator|.
name|addField
argument_list|(
name|SCORE_FIELD
argument_list|,
name|iterator
operator|.
name|score
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|responseWriter
operator|.
name|writeAllDocs
argument_list|(
name|info
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|responseWriter
operator|.
name|writeOther
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
name|responseWriter
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
comment|/**No ops implementation so that the implementing classes do not have to do it    */
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{}
DECL|class|IdxInfo
specifier|private
specifier|static
class|class
name|IdxInfo
block|{
DECL|field|schema
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|searcher
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|field|returnFields
name|Set
argument_list|<
name|String
argument_list|>
name|returnFields
decl_stmt|;
DECL|field|includeScore
name|boolean
name|includeScore
decl_stmt|;
DECL|method|IdxInfo
specifier|private
name|IdxInfo
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|returnFields
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|includeScore
operator|=
name|returnFields
operator|!=
literal|null
operator|&&
name|returnFields
operator|.
name|contains
argument_list|(
name|SCORE_FIELD
argument_list|)
expr_stmt|;
if|if
condition|(
name|returnFields
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|returnFields
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|||
operator|(
name|returnFields
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|includeScore
operator|)
operator|||
name|returnFields
operator|.
name|contains
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|returnFields
operator|=
literal|null
expr_stmt|;
comment|// null means return all stored fields
block|}
block|}
name|this
operator|.
name|returnFields
operator|=
name|returnFields
expr_stmt|;
block|}
block|}
DECL|method|getDoc
specifier|private
specifier|static
name|SolrDocument
name|getDoc
parameter_list|(
name|int
name|id
parameter_list|,
name|IdxInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
name|info
operator|.
name|searcher
operator|.
name|doc
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|SolrDocument
name|solrDoc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
for|for
control|(
name|Fieldable
name|f
range|:
operator|(
name|List
argument_list|<
name|Fieldable
argument_list|>
operator|)
name|doc
operator|.
name|getFields
argument_list|()
control|)
block|{
name|String
name|fieldName
init|=
name|f
operator|.
name|name
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|returnFields
operator|!=
literal|null
operator|&&
operator|!
name|info
operator|.
name|returnFields
operator|.
name|contains
argument_list|(
name|fieldName
argument_list|)
condition|)
continue|continue;
name|SchemaField
name|sf
init|=
name|info
operator|.
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|FieldType
name|ft
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sf
operator|!=
literal|null
condition|)
name|ft
operator|=
name|sf
operator|.
name|getType
argument_list|()
expr_stmt|;
name|Object
name|val
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|ft
operator|==
literal|null
condition|)
block|{
comment|// handle fields not in the schema
if|if
condition|(
name|f
operator|.
name|isBinary
argument_list|()
condition|)
name|val
operator|=
name|f
operator|.
name|binaryValue
argument_list|()
expr_stmt|;
else|else
name|val
operator|=
name|f
operator|.
name|stringValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
if|if
condition|(
name|BinaryResponseWriter
operator|.
name|KNOWN_TYPES
operator|.
name|contains
argument_list|(
name|ft
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
name|val
operator|=
name|ft
operator|.
name|toObject
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|val
operator|=
name|ft
operator|.
name|toExternal
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// There is a chance of the underlying field not really matching the
comment|// actual field type . So ,it can throw exception
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error reading a field from document : "
operator|+
name|solrDoc
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// if it happens log it and continue
continue|continue;
block|}
block|}
if|if
condition|(
name|sf
operator|!=
literal|null
operator|&&
name|sf
operator|.
name|multiValued
argument_list|()
operator|&&
operator|!
name|solrDoc
operator|.
name|containsKey
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|ArrayList
name|l
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|solrDoc
operator|.
name|addField
argument_list|(
name|fieldName
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|solrDoc
operator|.
name|addField
argument_list|(
name|fieldName
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|solrDoc
return|;
block|}
DECL|class|DocListInfo
specifier|public
specifier|static
class|class
name|DocListInfo
block|{
DECL|field|numFound
specifier|public
name|long
name|numFound
init|=
literal|0
decl_stmt|;
DECL|field|start
specifier|public
name|long
name|start
init|=
literal|0
decl_stmt|;
DECL|field|maxScore
specifier|public
name|Float
name|maxScore
init|=
literal|null
decl_stmt|;
DECL|method|DocListInfo
specifier|public
name|DocListInfo
parameter_list|(
name|long
name|numFound
parameter_list|,
name|long
name|start
parameter_list|,
name|Float
name|maxScore
parameter_list|)
block|{
name|this
operator|.
name|numFound
operator|=
name|numFound
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|maxScore
operator|=
name|maxScore
expr_stmt|;
block|}
block|}
comment|/**    *     * Users wanting to define custom {@link QueryResponseWriter}s that deal with    * {@link SolrInputDocument}s and {@link SolrDocumentList} should override the    * methods for this class. All the methods are w/o body because the user is left    * to choose which all methods are required for his purpose    */
DECL|class|SingleResponseWriter
specifier|public
specifier|static
specifier|abstract
class|class
name|SingleResponseWriter
block|{
comment|/**      * This method is called at the start of the {@link QueryResponseWriter}      * output. Override this method if you want to provide a header for your      * output, e.g., XML headers, etc.      *       * @throws IOException      *           if any error occurs.      */
DECL|method|start
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{ }
comment|/**      * This method is called at the start of processing a      * {@link SolrDocumentList}. Those that override this method are provided      * with {@link DocListInfo} object to use to inspect the output      * {@link SolrDocumentList}.      *       * @param info Information about the {@link SolrDocumentList} to output.      */
DECL|method|startDocumentList
specifier|public
name|void
name|startDocumentList
parameter_list|(
name|DocListInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{ }
comment|/**      * This method writes out a {@link SolrDocument}, on a doc-by-doc basis.      * This method is only called when {@link #isStreamingDocs()} returns true.      *       * @param solrDocument      *          The doc-by-doc {@link SolrDocument} to transform into output as      *          part of this {@link QueryResponseWriter}.      */
DECL|method|writeDoc
specifier|public
name|void
name|writeDoc
parameter_list|(
name|SolrDocument
name|solrDocument
parameter_list|)
throws|throws
name|IOException
block|{ }
comment|/**      * This method is called at the end of outputting a {@link SolrDocumentList}      * or on a doc-by-doc {@link SolrDocument} basis.      */
DECL|method|endDocumentList
specifier|public
name|void
name|endDocumentList
parameter_list|()
throws|throws
name|IOException
block|{ }
comment|/**      * This method defines how to output the {@link SolrQueryResponse} header      * which is provided as a {@link NamedList} parameter.      *       * @param responseHeader      *          The response header to output.      */
DECL|method|writeResponseHeader
specifier|public
name|void
name|writeResponseHeader
parameter_list|(
name|NamedList
name|responseHeader
parameter_list|)
throws|throws
name|IOException
block|{ }
comment|/**      * This method is called at the end of the {@link QueryResponseWriter}      * lifecycle. Implement this method to add a footer to your output, e.g., in      * the case of XML, the outer tag for your tag set, etc.      *       * @throws IOException      *           If any error occurs.      */
DECL|method|end
specifier|public
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{ }
comment|/**      * Define this method to control how output is written by this      * {@link QueryResponseWriter} if the output is not a      * {@link SolrInputDocument} or a {@link SolrDocumentList}.      *       * @param name      *          The name of the object to output.      * @param other      *          The object to output.      * @throws IOException      *           If any error occurs.      */
DECL|method|writeOther
specifier|public
name|void
name|writeOther
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|other
parameter_list|)
throws|throws
name|IOException
block|{ }
comment|/**      * Overriding this method to return false forces all      * {@link SolrInputDocument}s to be spit out as a {@link SolrDocumentList}      * so they can be processed as a whole, rather than on a doc-by-doc basis.      * If set to false, this method calls      * {@link #writeAllDocs(DocListInfo, List)}, else if set to true, then this      * method forces calling {@link #writeDoc(SolrDocument)} on a doc-by-doc      * basis. one      *       * @return True to force {@link #writeDoc(SolrDocument)} to be called, False      *         to force {@link #writeAllDocs(DocListInfo, List)} to be called.      */
DECL|method|isStreamingDocs
specifier|public
name|boolean
name|isStreamingDocs
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**      * Writes out all {@link SolrInputDocument}s . This is invoked only if      * {@link #isStreamingDocs()} returns false.      *       * @param info      *          Information about the {@link List} of {@link SolrDocument}s to      *          output.      * @param allDocs      *          A {@link List} of {@link SolrDocument}s to output.      * @throws IOException      *           If any error occurs.      */
DECL|method|writeAllDocs
specifier|public
name|void
name|writeAllDocs
parameter_list|(
name|DocListInfo
name|info
parameter_list|,
name|List
argument_list|<
name|SolrDocument
argument_list|>
name|allDocs
parameter_list|)
throws|throws
name|IOException
block|{ }
block|}
block|}
end_class
end_unit
