begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
begin_comment
comment|/**  * Works in conjunction with the SinkTokenizer to provide the ability to set aside tokens  * that have already been analyzed.  This is useful in situations where multiple fields share  * many common analysis steps and then go their separate ways.  *<p/>  * It is also useful for doing things like entity extraction or proper noun analysis as  * part of the analysis workflow and saving off those tokens for use in another field.  *  *<pre> SinkTokenizer sink1 = new SinkTokenizer(null); SinkTokenizer sink2 = new SinkTokenizer(null);  TokenStream source1 = new TeeTokenFilter(new TeeTokenFilter(new WhitespaceTokenizer(reader1), sink1), sink2); TokenStream source2 = new TeeTokenFilter(new TeeTokenFilter(new WhitespaceTokenizer(reader2), sink1), sink2);  TokenStream final1 = new LowerCaseFilter(source1); TokenStream final2 = source2; TokenStream final3 = new EntityDetect(sink1); TokenStream final4 = new URLDetect(sink2);  d.add(new Field("f1", final1)); d.add(new Field("f2", final2)); d.add(new Field("f3", final3)); d.add(new Field("f4", final4));  *</pre>  * In this example, sink1 and sink2 will both get tokens from both reader1 and reader2 after whitespace tokenizer    and now we can further wrap any of these in extra analysis, and more "sources" can be inserted if desired.  Note, the EntityDetect and URLDetect TokenStreams are for the example and do not currently exist in Lucene<p/>  *  * See http://issues.apache.org/jira/browse/LUCENE-1058  * @see SinkTokenizer  *  **/
end_comment
begin_class
DECL|class|TeeTokenFilter
specifier|public
class|class
name|TeeTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|sink
name|SinkTokenizer
name|sink
decl_stmt|;
DECL|method|TeeTokenFilter
specifier|public
name|TeeTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|SinkTokenizer
name|sink
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|sink
operator|=
name|sink
expr_stmt|;
block|}
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|reusableToken
operator|!=
literal|null
assert|;
name|Token
name|nextToken
init|=
name|input
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
decl_stmt|;
name|sink
operator|.
name|add
argument_list|(
name|nextToken
argument_list|)
expr_stmt|;
return|return
name|nextToken
return|;
block|}
block|}
end_class
end_unit
