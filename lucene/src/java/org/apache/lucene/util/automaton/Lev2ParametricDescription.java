begin_unit
begin_package
DECL|package|org.apache.lucene.util.automaton
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|// The following code was generated with the moman/finenight pkg
end_comment
begin_comment
comment|// This package is available under the MIT License, see NOTICE.txt
end_comment
begin_comment
comment|// for more details.
end_comment
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
name|automaton
operator|.
name|LevenshteinAutomata
operator|.
name|ParametricDescription
import|;
end_import
begin_comment
comment|/** Parametric description for generating a Levenshtein automaton of degree 2 */
end_comment
begin_class
DECL|class|Lev2ParametricDescription
class|class
name|Lev2ParametricDescription
extends|extends
name|ParametricDescription
block|{
annotation|@
name|Override
DECL|method|transition
name|int
name|transition
parameter_list|(
name|int
name|absState
parameter_list|,
name|int
name|position
parameter_list|,
name|int
name|vector
parameter_list|)
block|{
comment|// null absState should never be passed in
assert|assert
name|absState
operator|!=
operator|-
literal|1
assert|;
comment|// decode absState -> state, offset
name|int
name|state
init|=
name|absState
operator|/
operator|(
name|w
operator|+
literal|1
operator|)
decl_stmt|;
name|int
name|offset
init|=
name|absState
operator|%
operator|(
name|w
operator|+
literal|1
operator|)
decl_stmt|;
assert|assert
name|offset
operator|>=
literal|0
assert|;
if|if
condition|(
name|position
operator|==
name|w
condition|)
block|{
if|if
condition|(
name|state
operator|<
literal|3
condition|)
block|{
specifier|final
name|int
name|loc
init|=
name|vector
operator|*
literal|3
operator|+
name|state
decl_stmt|;
name|offset
operator|+=
name|unpack
argument_list|(
name|offsetIncrs0
argument_list|,
name|loc
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|state
operator|=
name|unpack
argument_list|(
name|toStates0
argument_list|,
name|loc
argument_list|,
literal|2
argument_list|)
operator|-
literal|1
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|position
operator|==
name|w
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|state
operator|<
literal|5
condition|)
block|{
specifier|final
name|int
name|loc
init|=
name|vector
operator|*
literal|5
operator|+
name|state
decl_stmt|;
name|offset
operator|+=
name|unpack
argument_list|(
name|offsetIncrs1
argument_list|,
name|loc
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|state
operator|=
name|unpack
argument_list|(
name|toStates1
argument_list|,
name|loc
argument_list|,
literal|3
argument_list|)
operator|-
literal|1
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|position
operator|==
name|w
operator|-
literal|2
condition|)
block|{
if|if
condition|(
name|state
operator|<
literal|11
condition|)
block|{
specifier|final
name|int
name|loc
init|=
name|vector
operator|*
literal|11
operator|+
name|state
decl_stmt|;
name|offset
operator|+=
name|unpack
argument_list|(
name|offsetIncrs2
argument_list|,
name|loc
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|state
operator|=
name|unpack
argument_list|(
name|toStates2
argument_list|,
name|loc
argument_list|,
literal|4
argument_list|)
operator|-
literal|1
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|position
operator|==
name|w
operator|-
literal|3
condition|)
block|{
if|if
condition|(
name|state
operator|<
literal|21
condition|)
block|{
specifier|final
name|int
name|loc
init|=
name|vector
operator|*
literal|21
operator|+
name|state
decl_stmt|;
name|offset
operator|+=
name|unpack
argument_list|(
name|offsetIncrs3
argument_list|,
name|loc
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|state
operator|=
name|unpack
argument_list|(
name|toStates3
argument_list|,
name|loc
argument_list|,
literal|5
argument_list|)
operator|-
literal|1
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|position
operator|==
name|w
operator|-
literal|4
condition|)
block|{
if|if
condition|(
name|state
operator|<
literal|30
condition|)
block|{
specifier|final
name|int
name|loc
init|=
name|vector
operator|*
literal|30
operator|+
name|state
decl_stmt|;
name|offset
operator|+=
name|unpack
argument_list|(
name|offsetIncrs4
argument_list|,
name|loc
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|state
operator|=
name|unpack
argument_list|(
name|toStates4
argument_list|,
name|loc
argument_list|,
literal|5
argument_list|)
operator|-
literal|1
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|state
operator|<
literal|30
condition|)
block|{
specifier|final
name|int
name|loc
init|=
name|vector
operator|*
literal|30
operator|+
name|state
decl_stmt|;
name|offset
operator|+=
name|unpack
argument_list|(
name|offsetIncrs5
argument_list|,
name|loc
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|state
operator|=
name|unpack
argument_list|(
name|toStates5
argument_list|,
name|loc
argument_list|,
literal|5
argument_list|)
operator|-
literal|1
expr_stmt|;
block|}
block|}
if|if
condition|(
name|state
operator|==
operator|-
literal|1
condition|)
block|{
comment|// null state
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
comment|// translate back to abs
return|return
name|state
operator|*
operator|(
name|w
operator|+
literal|1
operator|)
operator|+
name|offset
return|;
block|}
block|}
comment|// 1 vectors; 3 states per vector; array length = 3
DECL|field|toStates0
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|toStates0
init|=
operator|new
name|long
index|[]
comment|/*2 bits per value */
block|{
literal|0x23L
block|}
decl_stmt|;
DECL|field|offsetIncrs0
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|offsetIncrs0
init|=
operator|new
name|long
index|[]
comment|/*1 bits per value */
block|{
literal|0x0L
block|}
decl_stmt|;
comment|// 2 vectors; 5 states per vector; array length = 10
DECL|field|toStates1
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|toStates1
init|=
operator|new
name|long
index|[]
comment|/*3 bits per value */
block|{
literal|0x13688b44L
block|}
decl_stmt|;
DECL|field|offsetIncrs1
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|offsetIncrs1
init|=
operator|new
name|long
index|[]
comment|/*1 bits per value */
block|{
literal|0x3e0L
block|}
decl_stmt|;
comment|// 4 vectors; 11 states per vector; array length = 44
DECL|field|toStates2
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|toStates2
init|=
operator|new
name|long
index|[]
comment|/*4 bits per value */
block|{
literal|0x26a09a0a0520a504L
block|,
literal|0x2323523321a260a2L
block|,
literal|0x354235543213L
block|}
decl_stmt|;
DECL|field|offsetIncrs2
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|offsetIncrs2
init|=
operator|new
name|long
index|[]
comment|/*2 bits per value */
block|{
literal|0x5555520280000800L
block|,
literal|0x555555L
block|}
decl_stmt|;
comment|// 8 vectors; 21 states per vector; array length = 168
DECL|field|toStates3
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|toStates3
init|=
operator|new
name|long
index|[]
comment|/*5 bits per value */
block|{
literal|0x380e014a051404L
block|,
literal|0xe28245009451140L
block|,
literal|0x8a26880098a6268cL
block|,
literal|0x180a288ca0246213L
block|,
literal|0x494053284a1080e1L
block|,
literal|0x510265a89c311940L
block|,
literal|0x4218c41188a6509cL
block|,
literal|0x6340c4211c4710dL
block|,
literal|0xa168398471882a12L
block|,
literal|0x104c841c683a0425L
block|,
literal|0x3294472904351483L
block|,
literal|0xe6290620a84a20d0L
block|,
literal|0x1441a0ea2896a4a0L
block|,
literal|0x32L
block|}
decl_stmt|;
DECL|field|offsetIncrs3
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|offsetIncrs3
init|=
operator|new
name|long
index|[]
comment|/*2 bits per value */
block|{
literal|0x33300230c0000800L
block|,
literal|0x220ca080a00fc330L
block|,
literal|0x555555f832823380L
block|,
literal|0x5555555555555555L
block|,
literal|0x5555555555555555L
block|,
literal|0x5555L
block|}
decl_stmt|;
comment|// 16 vectors; 30 states per vector; array length = 480
DECL|field|toStates4
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|toStates4
init|=
operator|new
name|long
index|[]
comment|/*5 bits per value */
block|{
literal|0x380e014a051404L
block|,
literal|0xaa015452940L
block|,
literal|0x55014501000000L
block|,
literal|0x1843ddc771085c07L
block|,
literal|0x7141200040108405L
block|,
literal|0x52b44004c5313460L
block|,
literal|0x401080200063115cL
block|,
literal|0x85314c4d181c5048L
block|,
literal|0x1440190a3e5c7828L
block|,
literal|0x28a232809100a21L
block|,
literal|0xa028ca2a84203846L
block|,
literal|0xca0240010800108aL
block|,
literal|0xc7b4205c1580a508L
block|,
literal|0x1021090251846b6L
block|,
literal|0x4cb513862328090L
block|,
literal|0x210863128ca2b8a2L
block|,
literal|0x4e188ca024402940L
block|,
literal|0xa6b6c7c520532d4L
block|,
literal|0x8c41101451150219L
block|,
literal|0xa0c4211c4710d421L
block|,
literal|0x2108421094e15063L
block|,
literal|0x8f13c43708631044L
block|,
literal|0x18274d908c611631L
block|,
literal|0x1cc238c411098263L
block|,
literal|0x450e3a1d0212d0b4L
block|,
literal|0x31050242048108c6L
block|,
literal|0xfa318b42d07308eL
block|,
literal|0xa8865182356907c6L
block|,
literal|0x1ca410d4520c4140L
block|,
literal|0x2954e13883a0ca51L
block|,
literal|0x3714831044229442L
block|,
literal|0x93946116b58f2c84L
block|,
literal|0xc41109a5631a574dL
block|,
literal|0x1d4512d4941cc520L
block|,
literal|0x52848294c643883aL
block|,
literal|0xb525073148310502L
block|,
literal|0xa5356939460f7358L
block|,
literal|0x409ca651L
block|}
decl_stmt|;
DECL|field|offsetIncrs4
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|offsetIncrs4
init|=
operator|new
name|long
index|[]
comment|/*3 bits per value */
block|{
literal|0x20c0600000010000L
block|,
literal|0x2000040000000001L
block|,
literal|0x209204a40209L
block|,
literal|0x301b6c0618018618L
block|,
literal|0x207206186000186cL
block|,
literal|0x1200061b8e06dc0L
block|,
literal|0x480492080612010L
block|,
literal|0xa20204a040048000L
block|,
literal|0x1061a0000129124L
block|,
literal|0x1848349b680612L
block|,
literal|0xd26da0204a041868L
block|,
literal|0x2492492492496128L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x24924924L
block|}
decl_stmt|;
comment|// 32 vectors; 30 states per vector; array length = 960
DECL|field|toStates5
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|toStates5
init|=
operator|new
name|long
index|[]
comment|/*5 bits per value */
block|{
literal|0x380e014a051404L
block|,
literal|0xaa015452940L
block|,
literal|0x8052814501000000L
block|,
literal|0xb80a515450000e03L
block|,
literal|0x5140410842108426L
block|,
literal|0x71dc421701c01540L
block|,
literal|0x100421014610f7L
block|,
literal|0x85c0700550145010L
block|,
literal|0x94a271843ddc7710L
block|,
literal|0x1346071412108a22L
block|,
literal|0x3115c52b44004c53L
block|,
literal|0xc504840108020006L
block|,
literal|0x54d1001314c4d181L
block|,
literal|0x9081204239c4a71L
block|,
literal|0x14c5313460714124L
block|,
literal|0x51006428f971e0a2L
block|,
literal|0x4d181c5048402884L
block|,
literal|0xa3e5c782885314cL
block|,
literal|0x2809409482a8a239L
block|,
literal|0x2a84203846028a23L
block|,
literal|0x10800108aa028caL
block|,
literal|0xe1180a288ca0240L
block|,
literal|0x98c6b80e3294a108L
block|,
literal|0x2942328091098c10L
block|,
literal|0x11adb1ed08170560L
block|,
literal|0xa024004084240946L
block|,
literal|0x7b4205c1580a508cL
block|,
literal|0xa8c2968c71846b6cL
block|,
literal|0x4cb5138623280910L
block|,
literal|0x10863128ca2b8a20L
block|,
literal|0xe188ca0244029402L
block|,
literal|0x4e3294e288132d44L
block|,
literal|0x809409ad1218c39cL
block|,
literal|0xf14814cb51386232L
block|,
literal|0x514454086429adb1L
block|,
literal|0x32d44e188ca02440L
block|,
literal|0x8c390a6b6c7c5205L
block|,
literal|0xd4218c41409cd2aaL
block|,
literal|0x5063a0c4211c4710L
block|,
literal|0x10442108421094e1L
block|,
literal|0x31084711c4350863L
block|,
literal|0xbdef7bddf05918f2L
block|,
literal|0xc4f10dc218c41ef7L
block|,
literal|0x9d3642318458c63L
block|,
literal|0x70863104426098c6L
block|,
literal|0x8c6116318f13c43L
block|,
literal|0x41ef75dd6b5de4d9L
block|,
literal|0xd0212d0b41cc238cL
block|,
literal|0x2048108c6450e3a1L
block|,
literal|0x42d07308e3105024L
block|,
literal|0xdb591938f274084bL
block|,
literal|0xc238c41f77deefbbL
block|,
literal|0x1f183e8c62d0b41cL
block|,
literal|0x502a2194608d5a4L
block|,
literal|0xa318b42d07308e31L
block|,
literal|0xed675db56907c60fL
block|,
literal|0xa410d4520c41f773L
block|,
literal|0x54e13883a0ca511cL
block|,
literal|0x1483104422944229L
block|,
literal|0x20f2329447290435L
block|,
literal|0x1ef6f7ef6f7df05cL
block|,
literal|0xad63cb210dc520c4L
block|,
literal|0x58c695d364e51845L
block|,
literal|0xc843714831044269L
block|,
literal|0xe4d93946116b58f2L
block|,
literal|0x520c41ef717d6b17L
block|,
literal|0x83a1d4512d4941ccL
block|,
literal|0x50252848294c6438L
block|,
literal|0x144b525073148310L
block|,
literal|0xefaf7b591c20f275L
block|,
literal|0x941cc520c41f777bL
block|,
literal|0xd5a4e5183dcd62d4L
block|,
literal|0x4831050272994694L
block|,
literal|0x460f7358b5250731L
block|,
literal|0xf779bd6717b56939L
block|}
decl_stmt|;
DECL|field|offsetIncrs5
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|offsetIncrs5
init|=
operator|new
name|long
index|[]
comment|/*3 bits per value */
block|{
literal|0x20c0600000010000L
block|,
literal|0x40000000001L
block|,
literal|0xb6db6d4830180L
block|,
literal|0x4812900824800010L
block|,
literal|0x2092000040000082L
block|,
literal|0x618000b659254a40L
block|,
literal|0x86c301b6c0618018L
block|,
literal|0xdb01860061860001L
block|,
literal|0x81861800075baed6L
block|,
literal|0x186e381b70081cL
block|,
literal|0xe56dc02072061860L
block|,
literal|0x61201001200075b8L
block|,
literal|0x480000480492080L
block|,
literal|0x52b5248201848040L
block|,
literal|0x880812810012000bL
block|,
literal|0x4004800004a4492L
block|,
literal|0xb529124a20204aL
block|,
literal|0x49b68061201061a0L
block|,
literal|0x8480418680018483L
block|,
literal|0x1a000752ad26da01L
block|,
literal|0x4a349b6808128106L
block|,
literal|0xa0204a0418680018L
block|,
literal|0x492492497528d26dL
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|}
decl_stmt|;
comment|// state map
comment|//   0 -> [(0, 0)]
comment|//   1 -> [(0, 2)]
comment|//   2 -> [(0, 1)]
comment|//   3 -> [(0, 1), (1, 1)]
comment|//   4 -> [(0, 2), (1, 2)]
comment|//   5 -> [(0, 2), (2, 1)]
comment|//   6 -> [(0, 1), (2, 2)]
comment|//   7 -> [(0, 2), (2, 2)]
comment|//   8 -> [(0, 1), (1, 1), (2, 1)]
comment|//   9 -> [(0, 2), (1, 2), (2, 2)]
comment|//   10 -> [(0, 1), (2, 1)]
comment|//   11 -> [(0, 2), (3, 2)]
comment|//   12 -> [(0, 2), (1, 2), (3, 2)]
comment|//   13 -> [(0, 2), (1, 2), (2, 2), (3, 2)]
comment|//   14 -> [(0, 1), (2, 2), (3, 2)]
comment|//   15 -> [(0, 2), (3, 1)]
comment|//   16 -> [(0, 1), (3, 2)]
comment|//   17 -> [(0, 1), (1, 1), (3, 2)]
comment|//   18 -> [(0, 2), (1, 2), (3, 1)]
comment|//   19 -> [(0, 2), (2, 2), (3, 2)]
comment|//   20 -> [(0, 2), (2, 1), (3, 1)]
comment|//   21 -> [(0, 2), (2, 1), (4, 2)]
comment|//   22 -> [(0, 2), (1, 2), (4, 2)]
comment|//   23 -> [(0, 2), (1, 2), (3, 2), (4, 2)]
comment|//   24 -> [(0, 2), (2, 2), (3, 2), (4, 2)]
comment|//   25 -> [(0, 2), (3, 2), (4, 2)]
comment|//   26 -> [(0, 2), (1, 2), (2, 2), (4, 2)]
comment|//   27 -> [(0, 2), (1, 2), (2, 2), (3, 2), (4, 2)]
comment|//   28 -> [(0, 2), (4, 2)]
comment|//   29 -> [(0, 2), (2, 2), (4, 2)]
DECL|method|Lev2ParametricDescription
specifier|public
name|Lev2ParametricDescription
parameter_list|(
name|int
name|w
parameter_list|)
block|{
name|super
argument_list|(
name|w
argument_list|,
literal|2
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
operator|-
literal|1
block|,
literal|0
block|,
literal|0
block|,
operator|-
literal|1
block|,
literal|0
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|2
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|2
block|,
operator|-
literal|1
block|,
operator|-
literal|2
block|,
operator|-
literal|2
block|,
operator|-
literal|2
block|,
operator|-
literal|2
block|,
operator|-
literal|2
block|,
operator|-
literal|2
block|,
operator|-
literal|2
block|,
operator|-
literal|2
block|,
operator|-
literal|2
block|,
operator|-
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
