window.$BV||(function(f,l,c){var j={},p,s={},n={},i={},q={},t={},e=[],m=0;function d(v){var w=[].join.call(arguments," ");if(f.console&&console.log){if(l.all){console.log(w)}else{console.log.apply(console,arguments)}}else{if(f.Debug&&Debug.writeln){Debug.writeln(w)}else{if(f.opera&&opera.postError){opera.postError(w)}}}}j.log=d;function a(x,y){var v,w;if(x.length!==c){for(w=0;w<x.length;w++){y(w,x[w])}}else{for(v in x){y(v,x[v])}}}function r(z){var y,w,x,v;for(v=1;v<arguments.length;v++){if((y=arguments[v])!=null){for(w in y){if((x=y[w])!==c){z[w]=x}}}}return z}function b(v){m+=v;$bv("body").toggleClass("BVDIAjaxWait",m>0)}function g(){}j.Internal=r(g,{each:a,extend:r,exposeGlobals:function(v){a(v,function(w,x){if(/^(bv|BV)/.test(w)){f[w]=x}})},create:function(v){function w(){}w.prototype=v;return new w()},newLatch:function(v){var w=[];return{increment:function(){v++},release:function(){v--;while(v<=0&&w.length){(w.shift())()}},queue:function(x){if(v<=0){x()}else{w.push(x)}}}}});function o(w,v){return function(){return w.apply(null,v.concat([].slice.call(arguments,0)))}}function h(v){var x=[],w=g._require.s.contexts._.specified;a(v,function(B,A){if(!w[A]){var z=q[A],y=i[z||A];if(z){x.push(z)}if(y){a(y,function(D,C){w[C]=true})}}});if(x.length){g._require(x)}return v}function k(v,w){return function(){try{return w&&w.apply(null,arguments)}catch(x){d("Exception in "+v,x)}}}function u(){if($bv().jquery){$bv.ready()}else{p=true}}p=l.readyState==="complete";if(l.addEventListener){l.addEventListener("DOMContentLoaded",u,false);f.addEventListener("load",u,false)}j.docReady=u;r(g,{require:function(v,w){return g._require(h(v),w&&k("<unknown>",w))},define:function(v,w,x,y){g._require.def(v,h(x),k(v,o(y,w)))},modify:function(x,v,w,y,z){g._require.modify(x,v,h(y),z&&k(v,o(z,w)))},callAjax:function(x,w){var v=[].slice.call(arguments,0);b(1);g._require([x],k(x,function(y){b(-1);if(y){y.apply(null,v)}else{d("Bazaarvoice: error fetching url: "+x)}}),"bvajax")},ajaxCallback:function(v){e.push(v)},onModuleLoaded:function(w,x){if(x==="bvajax"){var v=e.shift();g._require.def(w,[],function(){return v},x)}},defineJQuery:function(v){g.define("jquery.core",[v.noConflict(true)],[],function(w){f.$bv=w;if(p){u()}return w})},getAlternateUrl:function(v){return t[v]},configureLoader:function(w,x,y,v){if(g._baseUrl){delete w.baseUrl}else{g._baseUrl=w.baseUrl}g._require(w);g._require(w,null,null,"bvajax");r(t,x);r(s,y);r(n,v)},configureAppLoader:function(z,v,y){if(typeof v!=="boolean"){y=v;v=false}var w=v?"-mobile":"";a(["global",z],function(B,C){var A=s[C+w]?C+w:C;if(s[A]){a(s[A],function(E,D){i[E]=D;for(var F=0;F<D.length;F++){if(!q[D[F]]){q[D[F]]=E}}});delete s[A]}});function x(B,C){var A={};A[C]=q[B]||B;g._require.modify(A)}a(["global",z],function(A,B){if(n[B]){a(n[B],x);delete n[B]}});if(y){a(y,x)}},configureFromWindow:function(v){if(f!=v&&v.$BV&&v.$BV.Internal._baseUrl){g._baseUrl=null;g.configureLoader({baseUrl:v.$BV.Internal._baseUrl})}}});f.$bv=function(v){var x,y,w=[];if(v&&(x=/^\s*#([^, ]+)\s*$/.exec(v))&&(y=l.getElementById(x[1]))){w.push(y)}else{if(v==="body"){w.push(y=l.body)}}w.text=function(){return y&&(y.textContent||y.innerText)};w.attr=function(z,A){if(A===c){return y&&y.getAttribute(z)}else{y&&y.setAttribute(z,""+A);return w}};w.toggleClass=function(z,B){if(y){var A=" "+(y.className||"")+" ";if(B){if(A.indexOf(" "+z+" ")<0){A+=" "+z;y.className=A.replace(/^\s+|\s+$/g,"")}}else{y.className=A.replace(" "+z,"").replace(/^\s+|\s+$/g,"")}}};return w};f.$BV=j}(window,document));
/*
* @license RequireJS Copyright (c) 2004-2010, The Dojo Foundation All Rights Reserved.
* Available via the MIT, GPL or new BSD license.
* see: http://github.com/jrburke/requirejs for details
*/
(function(n){var h="0.12.0",o={},r,v,g="_",u=[],f,y,x,k,t,j,q,e=/^(complete|loaded)$/,p=!!(typeof window!=="undefined"&&navigator&&document),l=!p&&typeof importScripts!=="undefined",w=Object.prototype.toString,d,c,a;function b(i){return w.call(i)==="[object Function]"}if(typeof n!=="undefined"){if(b(n)){return}else{j=n}}n=function(m,s,i){if(typeof m==="string"&&!b(s)){return n.get(m,s)}return n.def.apply(n,arguments)};c=n;n.def=function(O,G,B,M){var N=null,m,z,E,K,I,s,C,H,A,F,J,D,L;if(typeof O==="string"){D=O.indexOf("!");if(D!==-1){F=O.substring(0,D);O=O.substring(D+1,O.length)}if(!n.isArray(G)){M=B;B=G;G=[]}M=M||r.ctxName;m=r.contexts[M];if(m&&(m.defined[O]||m.waiting[O])){return n}}else{if(n.isArray(O)){M=B;B=G;G=O;O=null}else{if(n.isFunction(O)){B=O;M=G;O=null;G=[]}else{N=O;O=null;if(n.isFunction(G)){M=B;B=G;G=[]}M=M||N.context}}}M=M||r.ctxName;m=r.contexts[M];if(!m){z={contextName:M,config:{waitSeconds:7,baseUrl:r.baseUrl||"./",paths:{}},waiting:[],specified:{require:true,exports:true,module:true},loaded:{require:true},urlFetched:{},defined:{},modifiers:{}};z.defined.require=n;m=r.contexts[M]=z}if(N){if(N.baseUrl){if(N.baseUrl.charAt(N.baseUrl.length-1)!=="/"){N.baseUrl+="/"}}J=m.config.paths;n.mixin(m.config,N,true);if(N.paths){for(s in N.paths){if(!(s in o)){J[s]=N.paths[s]}}m.config.paths=J}if(N.priority){c(N.priority);m.config.priorityWait=N.priority}if(N.deps||N.callback){c(N.deps||[],N.callback)}if(!G){return n}}if(G){H=G;G=[];for(L=0;L<H.length;L++){G[L]=n.splitPrefix(H[L],O)}}C=m.waiting.push({name:O,deps:G,callback:B});if(O){m.waiting[O]=C-1;m.specified[O]=true;A=m.modifiers[O];if(A){c(A,M)}}if(O&&B&&!n.isFunction(B)){m.defined[O]=B}if(r.paused||m.config.priorityWait){(r.paused||(r.paused=[])).push([F,O,G,m])}else{n.checkDeps(F,O,G,m);n.checkLoaded(M)}if(O){m.loaded[O]=true}return n};n.mixin=function(s,m,i){for(var z in m){if(!(z in o)&&(!(z in s)||i)){s[z]=m[z]}}return n};n.version=h;r=n.s={ctxName:g,contexts:{},skipAsync:{},isBrowser:p,isPageLoaded:!p,readyCalls:[],doc:p?document:null};n.isBrowser=r.isBrowser;if(p){r.head=document.getElementsByTagName("head")[0];a=document.getElementsByTagName("base")[0];if(a){r.head=a.parentNode}}n.pause=function(){if(!r.paused){r.paused=[]}};n.resume=function(){var z,m,s;if(r.contexts[r.ctxName].config.priorityWait){return}if(r.paused){s=r.paused;delete r.paused;for(z=0;(m=s[z]);z++){n.checkDeps.apply(n,m)}}n.checkLoaded(r.ctxName)};n.checkDeps=function(m,s,C,A){var z,B;if(m){}else{for(z=0;(B=C[z]);z++){if(!A.specified[B.fullName]){A.specified[B.fullName]=true;if(B.prefix){}else{n.load(B.name,A.contextName)}}}}};n.modify=function(B,m,G,F,A){var i,z,C,D=(typeof B==="string"?A:m)||r.ctxName,s=r.contexts[D],E=s.modifiers;if(typeof B==="string"){C=E[B]||(E[B]=[]);if(!C[m]){C.push(m);C[m]=true}n.def(m,G,F,A)}else{for(i in B){if(!(i in o)){z=B[i];C=s.modifiers[i]||(s.modifiers[i]=[]);if(!C[z]){C.push(z);C[z]=true;if(s.specified[i]){c([z],D)}}}}}};n.isArray=function(i){return w.call(i)==="[object Array]"};n.isFunction=b;n.get=function(m,s){if(m==="exports"||m==="module"){throw new Error("require of "+m+" is not allowed.")}s=s||r.ctxName;var i=r.contexts[s].defined[m];if(i===undefined){throw new Error("require: module name '"+m+"' has not been loaded yet for context: "+s)}return i};n.load=function(s,B){var z=r.contexts[B],A=z.urlFetched,m=z.loaded,i;r.isDone=false;if(!m[s]){m[s]=false}i=n.nameToUrl(s,null,B);if(!A[i]){n.attach(i,B,s);A[i]=true}z.startTime=(new Date()).getTime()};n.jsExtRegExp=/\.js$/;n.normalizeName=function(m,s){var i;if(m.charAt(0)==="."){s=s.split("/");s=s.slice(0,s.length-1);m=s.concat(m.split("/"));for(v=0;(i=m[v]);v++){if(i==="."){m.splice(v,1);v-=1}else{if(i===".."){m.splice(v-1,2);v-=2}}}m=m.join("/")}return m};n.splitPrefix=function(m,s){var i=m.indexOf("!"),z=null;
if(i!==-1){z=m.substring(0,i);m=m.substring(i+1,m.length)}if(s){m=n.normalizeName(m,s)}return{prefix:z,name:m,fullName:z?z+"!"+m:m}};n.nameToUrl=function(m,z,B){var F,C,D,E,s,A=r.contexts[B].config;if(m.indexOf(":")!==-1||m.charAt(0)==="/"||n.jsExtRegExp.test(m)){return m}else{if(m.charAt(0)==="."){throw new Error("require.nameToUrl does not handle relative module names (ones that start with '.' or '..')")}else{F=A.paths;C=m.split("/");for(D=C.length;D>0;D--){E=C.slice(0,D).join("/");if(F[E]){C.splice(0,D,F[E]);break}}s=C.join("/")+(z||".js");return((s.charAt(0)==="/"||s.match(/^\w+:/))?"":A.baseUrl)+s}}};n.checkLoaded=function(R){var D=r.contexts[R||r.ctxName],G=D.config.waitSeconds*1000,I=G&&(D.startTime+G)<new Date().getTime(),P,B=D.defined,m=D.modifiers,z,O="",M=false,A=false,E,J,N,Q,C,L,K,F,H,s={};if(D.isCheckLoaded){return}if(D.config.priorityWait){J=true;for(Q=0;(N=D.config.priorityWait[Q]);Q++){if(!D.loaded[N]){J=false;break}}if(J){delete D.config.priorityWait;n.resume()}else{return}}D.isCheckLoaded=true;z=D.waiting;P=D.loaded;for(E in P){if(!(E in o)){M=true;if(!P[E]){if(I){O+=E+" "}else{A=true;break}}}}if(!M&&!z.length){D.isCheckLoaded=false;return}if(I&&O){H=new Error("require.js load timeout for modules: "+O);H.requireType="timeout";H.requireModules=O}if(A){D.isCheckLoaded=false;if(p||l){setTimeout(function(){n.checkLoaded(R)},50)}return}D.waiting=[];D.loaded={};for(E in m){if(!(E in o)){if(B[E]){n.execModifiers(E,s,z,D)}}}for(Q=0;(C=z[Q]);Q++){n.exec(C,s,z,D)}D.isCheckLoaded=false;if(D.waiting.length){n.checkLoaded(R)}else{if(u.length){}else{r.ctxName=g;r.isDone=true;if(n.callReady){n.callReady()}}}};n.exec=function(s,C,K,m){if(!s){return undefined}var i=s.name,A=s.callback,J=s.deps,D,H,B=m.defined,E,F=[],z,G=false,I;if(i){if(C[i]||i in B){return B[i]}C[i]=true}if(J){for(D=0;(H=J[D]);D++){I=H.name;if(I==="exports"){z=B[i]={};G=true}else{if(I==="module"){z={id:i,uri:i?n.nameToUrl(i,null,m.contextName):undefined}}else{z=I in B?B[I]:(C[I]?undefined:n.exec(K[K[I]],C,K,m))}}F.push(z)}}A=s.callback;if(A&&n.isFunction(A)){E=n.execCb(i,A,F);if(i){if(G){E=B[i]}else{if(i in B){throw new Error(i+" has already been defined")}else{B[i]=E}}}}n.execModifiers(i,C,K,m);return E};n.execCb=function(s,i,m){return i.apply(null,m)};n.execModifiers=function(D,C,E,A){var m=A.modifiers,B=m[D],z,s;if(B){for(s=0;s<B.length;s++){z=B[s];if(z in E){n.exec(E[E[z]],C,E,A)}}delete m[D]}};n.onScriptLoad=function(i){var s=i.currentTarget||i.srcElement,z,m;if(i.type==="load"||i.type==="error"||e.test(s.readyState)){z=s.getAttribute("data-requirecontext");m=s.getAttribute("data-requiremodule");$BV.Internal.onModuleLoaded(m,z);r.contexts[z].loaded[m]=true;n.checkLoaded(z);if(s.removeEventListener){s.removeEventListener("load",n.onScriptLoad,false);s.removeEventListener("error",n.onScriptLoad,false)}else{s.detachEvent("onreadystatechange",n.onScriptLoad)}}};n.attach=function(s,B,m,C,z){var A,i;if(p){C=C||n.onScriptLoad;A=document.createElement("script");A.type=z||"text/javascript";A.charset="utf-8";if(!r.skipAsync[s]){A.setAttribute("async","async")}A.setAttribute("data-requirecontext",B);A.setAttribute("data-requiremodule",m);if(A.addEventListener){A.addEventListener("load",C,false);A.addEventListener("error",C,false)}else{A.attachEvent("onreadystatechange",C)}A.src=s;return a?r.head.insertBefore(A,a):r.head.appendChild(A)}else{if(l){i=r.contexts[B].loaded;i[m]=false;importScripts(s);$BV.Internal.onModuleLoaded(m,B);i[m]=true}}return null};r.baseUrl=j&&j.baseUrl;if(p&&(!r.baseUrl||!r.head)){f=document.getElementsByTagName("script");if(j&&j.baseUrlMatch){x=j.baseUrlMatch}else{x=/(allplugins-|transportD-)?require\.js(\W|$)/i}for(v=f.length-1;v>-1&&(y=f[v]);v--){if(!r.head){r.head=y.parentNode}k=y.src;if(k){t=k.match(x);if(t){r.baseUrl=k.substring(0,t.index);break}}}}if(j){c(j)}$BV.Internal._require=n}($BV.Internal._require));
(function(f,g,a){var b=a._require,h=b.attach;b.attach=function(k,m,j){var l=a.getAlternateUrl(j);if(l&&m==="_"&&!c(j)){i(l,k,m,j)}else{h.apply(null,arguments)}};function i(l,n,m,j){var k=f.jQuery;h(l,m,j,function(r){var q=r.currentTarget||r.srcElement,p=r.type==="error",s=f.jQuery,o="1.4.4";if(!q.readyState||q.readyState==="loaded"||q.readyState==="complete"){if(!p&&j==="jquery.core"&&s&&s!==k&&s.fn&&s.fn.jquery===o){s.noConflict(true);if(!e(f.jQuery,s)){a.defineJQuery(s)}}if(d(j,m)){b.onScriptLoad(r)}else{h(n,m,j)}}})}function c(j){return g.all&&j==="jquery.core"}function e(n,j){try{if(!(n&&n.fn&&n.fn.jquery&&n.data)){return false}else{if(n.expando){return n.expando===j.expando}else{var m=g.createElement("div");n.data(m,"bv","bv");var k=m[j.expando]!=null;n.removeData(m,"bv");return k}}}catch(l){return true}}function d(j,l){var k=b.s.contexts[l||b.s.ctxName];return !!(k.defined[j]||k.waiting[j])}})(window,document,$BV.Internal);
$BV.Internal.configureLoader({"baseUrl":"http://www.purityproducts.com/","waitSeconds":20},{},{},{});
$BV.Internal.configureAppLoader("prr",false);
$BV.Internal.define("domUtils",[window,document],[],function(b,a){b.bvEscapeHtml=function(c){return String(c).replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;")};b.bvUnescapeHtml=function(c){return String(c).replace(/&lt;/g,"<").replace(/&gt;/g,">").replace(/&quot;/g,'"').replace(/&amp;/g,"&")};b.bvToggleDivWithIEControlsFrame=function(c,d,e){if(a.getElementById(c).style.display=="block"){bvCloseDivs(c,d)}else{bvExpandDivWithIEControlsFrame(c,d,e)}};b.bvExpandDivWithIEControlsFrame=function(c,d,i,e){if(!e){e=bvOpenDivs}e(c);var h=bvGetIEControlsFrame(d,i);if(h){var f=a.getElementById(c);if(f){f.parentNode.insertBefore(h,f);var g=bvGetLocation(c);h.style.width=g.width;h.style.height=g.height}e(d)}};b.bvGetIEControlsFrame=function(d,f){var e;var c=a.all&&((navigator.userAgent.indexOf("MSIE 6.")>-1)||(navigator.userAgent.indexOf("MSIE 5.5")>-1));if(c){e=a.getElementById(d);if(!e){e=a.createElement("iframe");e.id=d;e.title=f;e.src="javascript:''";e.scrolling="no";e.frameBorder="0";e.style.filter="progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)"}}return e};b.bvResizeIEControlsFrame=function(d){var g=a.getElementById(d);if(g){var e=g.nextSibling;var c=e.id;var f=bvGetLocation(c);g.style.width=f.width;g.style.height=f.height}};b.bvOpenDivs=function(){for(var c=0;c<arguments.length;c++){if(a.getElementById(arguments[c])){if(a.getElementById(arguments[c]).style.display!="block"){a.getElementById(arguments[c]).style.display="block"}}}};b.bvCloseDivs=function(){for(var c=0;c<arguments.length;c++){if(a.getElementById(arguments[c])){if(a.getElementById(arguments[c]).style.display!="none"){a.getElementById(arguments[c]).style.display="none"}}}};b.bvToggleVis=function(c){if(a.getElementById(c).style.display=="block"){a.getElementById(c).style.display="none"}else{a.getElementById(c).style.display="block"}};b.bvGetLocation=function(d){var f=a.getElementById(d);if(f){var c=f.offsetWidth;var e=f.offsetHeight;var i=0;var g=0;while(f){i+=f.offsetLeft;g+=f.offsetTop;f=f.offsetParent}return{left:i,top:g,width:c,height:e}}else{return{left:0,top:0,width:0,height:0}}};b.bvSetOpenerLocation=function(c,d){if(opener){if(opener.closed){b.open(c,"")}else{opener.location.href=c;if(d){b.close()}}return false}return true};b.bvReplaceTokensInSocialURL=function(c){return c.replace("__ROBOT__","no").replace("__TITLE__",encodeURIComponent(a.title))};b.bvFocus=function(e){a.getElementById(e).tabIndex="-1";if(navigator.userAgent.indexOf("MSIE 7")>-1){var c=a.getElementsByTagName("select");for(var d=0;d<c.length;d++){c[d].blur()}}else{a.getElementById(e).focus()}};b.bvStopPropagation=function(c){if(c){c.cancelBubble=true;if(c.stopPropagation){c.stopPropagation()}}}});
$BV.Internal.define("mouse-nojquery",[window,document],[],function(b,a){b.bvPointerPosX=0;b.bvPointerPosY=0;b.bvGetMouseXY=function(c){if(!c){c=b.event}if(a.documentElement&&a.documentElement.scrollTop){bvPointerPosX=c.clientX+a.documentElement.scrollLeft;bvPointerPosY=c.clientY+a.documentElement.scrollTop}else{if(a.body){bvPointerPosX=c.clientX+a.body.scrollLeft;bvPointerPosY=c.clientY+a.body.scrollTop}else{bvPointerPosX=c.pageX;bvPointerPosY=c.pageY}}if(bvPointerPosX<=0){bvPointerPosX=0}if(bvPointerPosY<=0){bvPointerPosY=0}};a.onmousemove=bvGetMouseXY;b.bvSetPos=function(d,c,e){if(e==undefined){e=0}a.getElementById(d).style.top=bvPointerPosY-e+"px";a.getElementById(d).style.left=bvPointerPosX-c+"px"}});
$BV.Internal.define("prr/reviewSubmitPRR",[window,document],[],function(b,a){b.bvToggleHiddenElement=function(c,d){var e=a.getElementById(d);if(e){if(c){e.style.display="block"}else{e.style.display="none"}}};b.bvReviewPhotoUploadSubmitted=false;b.changeSingleReviewButtonState=function(d,c){if(d){d.disabled=c}};b.bvSetReviewButtonsDisabled=function(c){var d=c?true:false;bvReviewPhotoUploadSubmitted=d;changeSingleReviewButtonState(a.getElementById("BVReviewPreviewButton"),d);changeSingleReviewButtonState(a.getElementById("BVReviewSubmitButton"),d)};b.bvPRRFindFrame=function(e){for(var c=0;c<b.frames.length;c++){try{if(b.frames[c].name==e){return b.frames[c]}}catch(d){}}return null};b.bvOnReviewPhotoUploadSubmit=function(){bvSetReviewButtonsDisabled(true);var h=a.getElementById("BVPhotoUploadHideOnUpload");if(h){h.style.display="none"}var l=a.getElementById("BVPhotoUploadShowOnUpload");if(l){l.style.display="block"}var c=a.getElementById("BVVisiblePhotoUploadInput");var d=bvPRRFindFrame("BVPhotoUploadFrame");var i=d.document.getElementById("BVPhotoUploadInput");var g=i.name;var e=a.all;var k=navigator.userAgent.indexOf("Safari")!=-1;if(!(k||e)){var f=d.document.getElementById("BVPhotoUploadDiv");while(i.firstChild){i.removeChild(i.firstChild)}var j=c.cloneNode(true);j.name=g;i.appendChild(j)}d.document.getElementById("BVPhotoUploadAction").value="upload";d.document.forms.BVPhotoUploadForm.submit()};b.bvOnReviewPhotoUploadBrowse=function(){var c=a.all;var d=navigator.userAgent.indexOf("Safari")!=-1;if(c||d){var e=bvPRRFindFrame("BVPhotoUploadFrame");e.document.getElementById("BVPhotoUploadInput").click()}};b.bvOnReviewPhotoUploadRemove=function(e){var g=bvPRRFindFrame("BVPhotoUploadFrame");g.document.getElementById("BVPhotoUploadAction").value="remove";g.document.getElementById("BVPhotoUploadParams").value=e;g.document.forms.BVPhotoUploadForm.submit();for(var f=e;;f++){var d=a.getElementById("BVPhotoCaption"+f);var c=a.getElementById("BVPhotoCaption"+(f+1));if(!c){d.value="";break}d.value=c.value}};b.bvOnReviewPhotoUploadSelect=function(){var c=a.all;var d=navigator.userAgent.indexOf("Safari")!=-1;if(c||d){var f=a.getElementById("BVPhotoUploadInput");var e=b.parent.document.getElementById("BVVisiblePhotoUploadInput");e.value=f.value}bvFindElementById("BVVisiblePhotoUploadSubmit").disabled=false};b.bvOnReviewPhotoCaptionEdit=function(c){var e=a.getElementById("BVVisiblePhotoCaption"+c);var d=a.getElementById("BVPhotoCaption"+c);d.value=e.value};b.bvReviewPhotoUploadCheckLoad=function(){bvSetReviewButtonsDisabled(false);var c=bvPRRFindFrame("BVPhotoUploadFrame");if(!c.document||!c.document.getElementById("BVPhotoUploadAction")){a.getElementById("BVPhotoUploadVisibleTarget").style.display="none";a.getElementById("BVPhotoUploadFatalError").style.display="block";return false}return true};b.BvGlobalCounter=function(){};BvGlobalCounter.value=0;b.bvGetMouseX=function(d){var c;if(a.all){c=d.clientX+b.parent.document.body.scrollLeft}else{c=d.pageX}if(c<0){c=0}return c};b.BvRatingBar=function(i){var p=i;var g=15;var m=14;var h=0.5;var j=5;var k=0;var q="Stars";var r="Star";var n=[" ","Poor","Fair","Average","Good","Excellent"];var o;var d="";var e;var l;var f=false;var c=false;this.setSpecificity=function(s){h=s};this.setBGWidth=function(s){g=s};this.setBGHeight=function(s){m=s};this.setMaxRating=function(s){j=s};this.setMinRating=function(s){k=s};this.setSparkleImage=function(s){d=s};this.setRatingLegend=function(s){if(typeof s.length==undefined){throw"Legend argument must be an array."}if(s.length!=(j-k+1)){throw"Legend argument cannot have "+s.length+" elements for "+(j-k)+" ratings."}n=s};this.getRatingLegendValue=function(s){return n[s-k]};this.setRatingType=function(t,s){if(!s){s=t+"s"}r=t;q=s};this.initializeValue=function(w){c=true;var t=(Math.ceil(w/h)*100*h)/100;if(t>j){t=j}else{if(t<k){t=k}}o=t;var s=t*g;b.parent.document.getElementById(i+"Filled").style.width=s+"px";if(l){var v=b.parent.document.getElementById(l);if(v){v.value=o}}else{var y=b.parent.document.getElementById(p+"Value");
if(y){y.value=o}}if(e){if(bvFindElementById(e)){if(o==1){bvFindElementById(e).innerHTML=o+" "+r}else{bvFindElementById(e).innerHTML=o+" "+q}}}else{var x=bvFindElementById(p+"Display");if(x){if(o==1){x.innerHTML=o+" "+r}else{x.innerHTML=o+" "+q}var u=bvFindElementById(p+"Legend");if(u){u.innerHTML=this.getRatingLegendValue(o)}}}};this.resizeTable=function(v,u){if(!v){v=b.parent.event}var w=b.parent.document.getElementById(p+"RatingBar");var s=w.style.width;var x=bvGetMouseX(v)-bvFindPosX(w);var t=(Math.ceil(x/g/h)*100*h)/100;if(t>j){t=j}else{if(t<k){t=k}}s=t*g;if(s<1){s=1}b.parent.document.getElementById(u).style.width=s+"px";o=t};this.setRating=function(t){this.updateRating(t,p+"Filled");c=true;var s=d+"?i="+BvGlobalCounter.value++;b.parent.document.getElementById(p+"Filled").style.background="url("+s+")"};this.updateRating=function(v,u,x){this.resizeTable(v,u);if(!x){if(l){var t=b.parent.document.getElementById(l);if(t){t.value=o}}else{var z=b.parent.document.getElementById(p+"Value");if(z){z.value=o}}}if(e){var y=bvFindElementById(e);if(y){if(o==1){y.innerHTML=o+" "+r}else{y.innerHTML=o+" "+q}}}else{var w=bvFindElementById(p+"Display");if(w){if(o==1){w.innerHTML=o+" "+r}else{w.innerHTML=o+" "+q}var s=bvFindElementById(p+"Legend");if(s){s.innerHTML=this.getRatingLegendValue(o)}}}};this.startSlide=function(){f=true};this.stopSlide=function(){if(f){var s=d+"?i="+BvGlobalCounter.value++;b.parent.document.getElementById(p+"Filled").style.background="url("+s+")";f=false}};this.doSlide=function(s){if(f){this.updateRating(s,p+"Filled");c=true}else{if(!c){this.updateRating(s,p+"Hover",true)}}};this.resetHover=function(){b.parent.document.getElementById(p+"Hover").style.width="1px";if(!c){if(e){var u=b.parent.document.getElementById(e);if(u){u.innerHTML=""}}else{var t=b.parent.document.getElementById(p+"Display");if(t){t.innerHTML="";var s=b.parent.document.getElementById(p+"Legend");if(s){s.innerHTML=""}}}}};this.init=function(){var s=g*j;b.parent.document.getElementById(p+"RatingBar").style.width=s+"px";b.parent.document.getElementById(p+"Filled").style.height=m+"px";if(b.parent.document.all){b.parent.document.attachEvent("onmouseup",this.stopSlide)}else{b.parent.document.addEventListener("mouseup",this.stopSlide,true)}}};b.bvFindPosY=function(d){var c=0;if(d.offsetParent){while(d.offsetParent){c+=d.offsetTop;d=d.offsetParent}}else{if(d.y){c+=d.y}}return c};b.bvFindPosX=function(c){var d=0;if(c.offsetParent){while(c){d+=c.offsetLeft;c=c.offsetParent}}else{if(c.x){d+=c.x}}return d};b.bvGetAncestorID=function(d){if(!d){d=b.event}var f=bvGetClickedElement(d);var c=f;var g=null;while(c&&!g){if(c.parentNode){g=c.getAttribute("id")}c=c.parentNode}return g};b.bvIsAncestorOf=function(f,c){if(!f){f=b.event}var g=bvGetClickedElement(f);var d=g;var h=null;while(d&&(h!=c)){if(d.parentNode&&d.id){h=d.getAttribute("id")}if(h==c){return true}d=d.parentNode}return false};b.bvGetClickedElement=function(d){if(!d){d=b.event}var c;if(d.target){c=d.target}else{if(d.srcElement){c=d.srcElement}}if(c.nodeType==3){c=c.parentNode}return c};b.BvPopupNote=function(c,e){var d=b.parent.document.getElementById(e);var f=b.parent.document.getElementById(c);if(!f&&!d){d=a.getElementById(e);f=a.getElementById(c)}this.setNote=function(g){d=g};this.setNoteToggle=function(g){f=g};this.clickToggle=function(){if(d.style.display=="block"){d.style.display="none"}else{var g=bvFindPosY(f);var h=bvFindPosX(f);d.style.top=g+f.offsetHeight;d.style.left=h;d.style.display="block"}};this.clickOutside=function(g){if(!g){g=b.parent.event}var h=bvGetAncestorID(g);if(!bvIsAncestorOf(g,d.id)&&h!=f.id){if(d.style.display=="block"){d.style.display="none"}}};this.init=function(){if(b.parent.document.all){b.parent.document.attachEvent("onclick",this.clickOutside);f.attachEvent("onclick",this.clickToggle)}else{f.addEventListener("click",this.clickToggle,true);b.parent.document.addEventListener("click",this.clickOutside,false)}}};b.BVMarkSelectedRadioIfPreviouslySet=function(f,g,c){var i=b.parent.document.getElementById(g+"Value");
if(i&&i.value>0){var h=b.parent.document.getElementById(g+"-"+(i.value-1));var d=b.parent.document.getElementById(g+"-"+(i.value-1)+"-label");var e=b.parent.document.getElementById(g+"Display");BVHighlightRadioSelection(e,d,c);h.checked="true"}};b.BVSetRadioSelection=function(e,h,k,f,l,m,c,d){if(!e||!h||!k||!f||l<0||m<1){return}if(e.checked){var j=0;for(j=0;j<m;j++){var g=b.parent.document.getElementById(c+"-"+j+"-label");g.className="BVRadioLabelUnselected BVRadioLabelUnselected"+d}BVHighlightRadioSelection(h,f,d);k.value=l+1}};b.BVHighlightRadioSelection=function(e,d,c){e.innerHTML=d.innerHTML;d.className="BVRadioLabelSelected BVRadioLabelSelected"+c};b.BvSlider=function(e,h,g,j,c,i,f){if(!e){return}this._range=new BvRange();this._range.setExtent(0);this._blockIncrement=1;this._unitIncrement=1;this._timer=new BvTimer(100);this._noValueLabel=c;this._valueLabels=i;this._staticResourcesPath=f;if(BvSlider.isSupported&&e){this.document=e.ownerDocument||e.document;this.element=e;this.element.slider=this;this.element.unselectable="on";this.element.className="horizontal "+this.classNameTag+" "+this.element.className;this.line=this.document.createElement("DIV");this.line.className="line";this.line.unselectable="on";this.line.appendChild(this.document.createElement("DIV"));this.element.appendChild(this.line);this.handle=this.document.createElement("IMG");this.handle.className="handle";this.handle.id=this.element.id+"_sliderImage";this.handle.src=this._staticResourcesPath+"/sliderHandle.gif";if(this.handle.tabIndex){this.handle.tabIndex=0}this.element.appendChild(this.handle)}this.input=h;this.hiddenElement=j;this.displayDiv=g;var d=this;this._range.onchange=function(){d.recalculate();if(typeof d.onchange=="function"){d.onchange()}};if(BvSlider.isSupported&&e){if(this.element.addEventListener){this.element.addEventListener("focus",BvSlider.eventHandlers.onfocus,true);this.element.addEventListener("blur",BvSlider.eventHandlers.onblur,true);this.element.addEventListener("mousedown",BvSlider.eventHandlers.onmousedown,true);this.element.addEventListener("mouseover",BvSlider.eventHandlers.onmouseover,true);this.element.addEventListener("mouseout",BvSlider.eventHandlers.onmouseout,true);this.element.addEventListener("keydown",BvSlider.eventHandlers.onkeydown,true);this.element.addEventListener("keyup",BvSlider.eventHandlers.onkeypress,true);this.element.addEventListener("mousewheel",BvSlider.eventHandlers.onmousewheel,true)}else{this.element.onfocus=BvSlider.eventHandlers.onfocus;this.element.onblur=BvSlider.eventHandlers.onblur;this.element.onmousedown=BvSlider.eventHandlers.onmousedown;this.element.onmouseover=BvSlider.eventHandlers.onmouseover;this.element.onmouseout=BvSlider.eventHandlers.onmouseout;this.element.onkeydown=BvSlider.eventHandlers.onkeydown;this.element.onkeypress=BvSlider.eventHandlers.onkeypress;this.element.onmousewheel=BvSlider.eventHandlers.onmousewheel}this.element.onselectstart=function(){return false};this._timer.ontimer=function(){d.ontimer()};setTimeout(function(){d.recalculate()},1)}else{this.input.onchange=function(k){d.setValue(d.input.value)}}};BvSlider.isSupported=typeof a.createElement!="undefined"&&typeof a.documentElement!="undefined"&&typeof a.documentElement.offsetWidth=="number";BvSlider.eventHandlers={getEvent:function(f,d){if(!f){if(d){f=d.document.parentWindow.event}else{f=b.event}}if(!f.srcElement){var c=f.target;while(c&&c.nodeType!=1){d=c.parentNode}f.srcElement=c}if(typeof f.offsetX=="undefined"){f.offsetX=f.layerX;f.offsetY=f.layerY}return f},getDocument:function(c){if(c.target){return c.target.ownerDocument}return c.srcElement.document},getSlider:function(d){var c=d.target||d.srcElement;while(c&&!c.slider){c=c.parentNode}if(c){return c.slider}return null},getLine:function(d){var c=d.target||d.srcElement;while(c&&c.className!="line"){c=c.parentNode}return c},getHandle:function(f){var d=f.target||f.srcElement;var c=/handle/;while(d&&!c.test(d.className)){d=d.parentNode}return d},onfocus:function(d){var c=this.slider;
c._focused=true;if(c.handle.src!=c._staticResourcesPath+"/sliderHandleHover.gif"){bvSwapImage(c.handle.id,c._staticResourcesPath+"/sliderHandleHover.gif",b.parent.document)}},onblur:function(d){var c=this.slider;c._focused=false;bvSwapImgRestore()},onmouseover:function(d){d=BvSlider.eventHandlers.getEvent(d,this);var c=this.slider;if(d.srcElement==c.handle){if(c.handle.src!=c._staticResourcesPath+"/sliderHandleHover.gif"){bvSwapImage(c.handle.id,c._staticResourcesPath+"/sliderHandleHover.gif",b.parent.document)}}},onmouseout:function(d){d=BvSlider.eventHandlers.getEvent(d,this);var c=this.slider;if(d.srcElement==c.handle&&!c._focused){bvSwapImgRestore()}},onmousedown:function(g){g=BvSlider.eventHandlers.getEvent(g,this);var c=this.slider;if(c.element.focus){c.element.focus()}BvSlider._currentInstance=c;var f=c.document;if(f.addEventListener){f.addEventListener("mousemove",BvSlider.eventHandlers.onmousemove,true);f.addEventListener("mouseup",BvSlider.eventHandlers.onmouseup,true);if(c.handle.focus){c.handle.focus()}}else{if(f.attachEvent){f.attachEvent("onmousemove",BvSlider.eventHandlers.onmousemove);f.attachEvent("onmouseup",BvSlider.eventHandlers.onmouseup);f.attachEvent("onlosecapture",BvSlider.eventHandlers.onmouseup);c.element.setCapture()}}if(BvSlider.eventHandlers.getHandle(g)){BvSlider._sliderDragData={screenX:g.screenX,screenY:g.screenY,dx:g.screenX-c.handle.offsetLeft,dy:g.screenY-c.handle.offsetTop,startValue:c.getValue(),slider:c}}else{var d=BvSlider.eventHandlers.getLine(g);c._mouseX=g.offsetX+(d?c.line.offsetLeft:0);c._mouseY=g.offsetY+(d?c.line.offsetTop:0);c._increasing=null;c.ontimer()}},onmousemove:function(i){i=BvSlider.eventHandlers.getEvent(i,this);var f;if(BvSlider._sliderDragData){f=BvSlider._sliderDragData.slider;var c=f.getMaximum()-f.getMinimum();var d,j,h;d=f.element.offsetWidth-f.handle.offsetWidth;j=i.screenX-BvSlider._sliderDragData.dx;h=Math.abs(i.screenY-BvSlider._sliderDragData.screenY)>100;f.setRestrictedValue(h?BvSlider._sliderDragData.startValue:f.getMinimum()+c*j/d);return false}else{f=BvSlider._currentInstance;if(f){var g=BvSlider.eventHandlers.getLine(i);f._mouseX=i.offsetX+(g?f.line.offsetLeft:0);f._mouseY=i.offsetY+(g?f.line.offsetTop:0)}}},onmouseup:function(f){f=BvSlider.eventHandlers.getEvent(f,this);var c=BvSlider._currentInstance;var d=c.document;if(d.removeEventListener){d.removeEventListener("mousemove",BvSlider.eventHandlers.onmousemove,true);d.removeEventListener("mouseup",BvSlider.eventHandlers.onmouseup,true)}else{if(d.detachEvent){d.detachEvent("onmousemove",BvSlider.eventHandlers.onmousemove);d.detachEvent("onmouseup",BvSlider.eventHandlers.onmouseup);d.detachEvent("onlosecapture",BvSlider.eventHandlers.onmouseup);c.element.releaseCapture()}}if(BvSlider._sliderDragData){BvSlider._sliderDragData=null}else{c._timer.stop();c._increasing=null}BvSlider._currentInstance=null},onkeydown:function(f){f=BvSlider.eventHandlers.getEvent(f,this);var d=this.slider;var c=f.keyCode;switch(c){case 33:d.setRestrictedValue(d.getValue()+d.getBlockIncrement());break;case 34:d.setRestrictedValue(d.getValue()-d.getBlockIncrement());break;case 35:d.setRestrictedValue(d.getMaximum());break;case 36:d.setRestrictedValue(d.getMinimum());break;case 38:case 39:d.setRestrictedValue(d.getValue()+d.getUnitIncrement());break;case 37:case 40:d.setRestrictedValue(d.getValue()-d.getUnitIncrement());break}if(c>=33&&c<=40){return false}},onkeypress:function(d){d=BvSlider.eventHandlers.getEvent(d,this);var c=d.keyCode;if(c>=33&&c<=40){return false}},onmousewheel:function(d){d=BvSlider.eventHandlers.getEvent(d,this);var c=this.slider;if(c._focused){c.setRestrictedValue(c.getValue()+d.wheelDelta/120*c.getUnitIncrement());return false}}};BvSlider.prototype.classNameTag="dynamic-slider-control";BvSlider.prototype.setValue=function(c){this._range.setValue(c);this.input.value=this.getValue()};BvSlider.prototype.setRestrictedValue=function(c){if(c<this.getMinimum()){this.setValue(this.getMinimum())}else{if(c>this.getMaximum()){this.setValue(this.getMaximum())
}else{this.setValue(c)}}};BvSlider.prototype.getValue=function(){return this._range.getValue()};BvSlider.prototype.isValueSet=function(){return this._range.isValueSet()};BvSlider.prototype.setMinimum=function(c){this._range.setMinimum(c);this.input.value=this.getValue()};BvSlider.prototype.getMinimum=function(){return this._range.getMinimum()};BvSlider.prototype.setMaximum=function(c){this._range.setMaximum(c);this.input.value=this.getValue()};BvSlider.prototype.getMaximum=function(){return this._range.getMaximum()};BvSlider.prototype.setUnitIncrement=function(c){this._unitIncrement=c};BvSlider.prototype.getUnitIncrement=function(){return this._unitIncrement};BvSlider.prototype.setBlockIncrement=function(c){this._blockIncrement=c};BvSlider.prototype.getBlockIncrement=function(){return this._blockIncrement};BvSlider.prototype.recalculate=function(){if(!BvSlider.isSupported||!this.element){return}var e=this.element.offsetWidth;var g=this.element.offsetHeight;var c=this.handle.offsetWidth;var f=this.handle.offsetHeight;var i=this.line.offsetWidth;var d=this.line.offsetHeight;this.handle.style.left=(e-c)*(this.getValue()-this.getMinimum())/(this.getMaximum()-this.getMinimum())+"px";this.handle.style.top=(g-f)/2+"px";this.line.style.top=(g-d)/2+"px";this.line.style.left=c/2+"px";this.line.style.right=c/2+"px";this.line.style.width=Math.max(0,e-c-2)+"px";this.line.firstChild.style.width=Math.max(0,e-c-4)+"px"};BvSlider.prototype.ontimer=function(){var d=this.handle.offsetWidth;var f=this.handle.offsetHeight;var c=this.handle.offsetLeft;var e=this.handle.offsetTop;if(this._mouseX>c+d&&(this._increasing===null||this._increasing)){this.setRestrictedValue(this.getValue()+this.getBlockIncrement());this._increasing=true}else{if(this._mouseX<c&&(this._increasing===null||!this._increasing)){this.setRestrictedValue(this.getValue()-this.getBlockIncrement());this._increasing=false}}this._timer.start()};BvSlider.prototype.displayVerb=function(){var c=Math.floor((this.getValue()-1)/10)+1;if(!this.isValueSet()||(c-1)>=this._valueLabels.length){this.displayDiv.innerHTML=this._noValueLabel;this.displayDiv.className="BVSliderCurrentValueUnset";this.hiddenElement.value=""}else{this.displayDiv.innerHTML=this._valueLabels[c-1];this.displayDiv.className="BVSliderCurrentValue";this.hiddenElement.value=c}};b.bvSwapImgRestore=function(){var e,c,d=a.BV_sr;for(e=0;d&&e<d.length&&(c=d[e])&&c.oSrc;e++){c.src=c.oSrc}};b.bvSetArrow=function(e,c,d){if(a.getElementById(c).style.display=="block"){a.getElementById(e).src=d+"/expandedicon.gif"}else{a.getElementById(e).src=d+"/collapsedicon.gif"}};b.bvTextCounter=function(d,h,f,g,c,e){d=d.replace(/\s+/g," ").replace(/^ /g,"").replace(/ $/g,"");if(!d||d.length===0){a.getElementById(h).innerHTML=g.replace(/\{0\}/,f)}else{if(d.length<f){a.getElementById(h).innerHTML=c.replace(/\{0\}/,f-d.length)}else{a.getElementById(h).innerHTML=e}}};b.bvToggleProdBlurb=function(d,c){a.getElementById(d).style.display="block";a.getElementById(c).style.display="none"};b.BvRange=function(){this._valueSet=false;this._value=35;this._minimum=1;this._maximum=70;this._extent=0;this._isChanging=false};BvRange.prototype.setValue=function(c){c=Math.round(parseFloat(c));if(isNaN(c)){return}if(this._value!=c){if(!c){this._value=35;this._valueSet=false}else{this._valueSet=true;if(c+this._extent>this._maximum){this._value=this._maximum-this._extent}else{if(c<this._minimum){this._value=this._minimum}else{this._value=c}}if(!this._isChanging&&typeof this.onchange=="function"){this.onchange()}}}};BvRange.prototype.getValue=function(){return this._value};BvRange.prototype.isValueSet=function(){return this._valueSet};BvRange.prototype.setExtent=function(c){if(this._extent!=c){if(c<0){this._extent=0}else{if(this._value+c>this._maximum){this._extent=this._maximum-this._value}else{this._extent=c}}if(!this._isChanging&&typeof this.onchange=="function"){this.onchange()}}};BvRange.prototype.getExtent=function(){return this._extent};BvRange.prototype.setMinimum=function(c){if(this._minimum!=c){var d=this._isChanging;
this._isChanging=true;this._minimum=c;if(c>this._value){this.setValue(c)}if(c>this._maximum){this._extent=0;this.setMaximum(c);this.setValue(c)}if(c+this._extent>this._maximum){this._extent=this._maximum-this._minimum}this._isChanging=d;if(!this._isChanging&&typeof this.onchange=="function"){this.onchange()}}};BvRange.prototype.getMinimum=function(){return this._minimum};BvRange.prototype.setMaximum=function(d){if(this._maximum!=d){var c=this._isChanging;this._isChanging=true;this._maximum=d;if(d<this._value){this.setValue(d-this._extent)}if(d<this._minimum){this._extent=0;this.setMinimum(d);this.setValue(this._maximum)}if(d<this._minimum+this._extent){this._extent=this._maximum-this._minimum}if(d<this._value+this._extent){this._extent=this._maximum-this._value}this._isChanging=c;if(!this._isChanging&&typeof this.onchange=="function"){this.onchange()}}};BvRange.prototype.getMaximum=function(){return this._maximum};b.BvTimer=function(c){this._pauseTime=typeof c=="undefined"?1000:c;this._timer=null;this._isStarted=false};BvTimer.prototype.start=function(){if(this.isStarted()){this.stop()}var c=this;this._timer=setTimeout(function(){if(typeof c.ontimer=="function"){c.ontimer()}},this._pauseTime);this._isStarted=false};BvTimer.prototype.stop=function(){if(this._timer){clearTimeout(this._timer)}this._isStarted=false};BvTimer.prototype.isStarted=function(){return this._isStarted};BvTimer.prototype.getPauseTime=function(){return this._pauseTime};BvTimer.prototype.setPauseTime=function(c){this._pauseTime=c};b.bvFindElementById=function(c){var d=null;if(b.parent&&b.parent.document){d=b.parent.document.getElementById(c)}if(!d){d=a.getElementById(c)}return d};b.bvRollHighlight=function(f,e,c,d){a.getElementById(f).style.backgroundImage=e;a.getElementById(c).className=d};b.bvPreloadImages=function(){var g=a;if(g.images){if(!g.BV_p){g.BV_p=[]}var f;var e=g.BV_p.length;var c=arguments;for(f=0;f<c.length;f++){if(c[f].indexOf("#")){g.BV_p[e]=new Image();g.BV_p[e++].src=c[f]}}}};b.bvFindObj=function(h,g){var f,e,c;if(!g){g=a}if((f=h.indexOf("?"))>0&&parent.frames.length){g=parent.frames[h.substring(f+1)].document;h=h.substring(0,f)}if(!(c=g[h])&&g.all){c=g.all[h]}for(e=0;!c&&e<g.forms.length;e++){c=g.forms[e][h]}for(e=0;!c&&g.layers&&e<g.layers.length;e++){c=bvFindObj(h,g.layers[e].document)}if(!c&&g.getElementById){c=g.getElementById(h)}return c};b.bvSwapImage=function(){var c,e;var d=arguments;if(!a.BV_sr){a.BV_sr=[]}var g=a.BV_sr;var f=g.length;if(d[2]){e=d[2]}else{e=null}if((c=bvFindObj(d[0],e))){g[f++]=c;if(!c.oSrc){c.oSrc=c.src}c.src=d[1]}};b.bvSetOpenerLocation=function(c,d){if(opener){if(opener.closed){b.open(c,"")}else{opener.location.href=c;if(d){b.close()}}return false}return true};b.bvInitTags=function(d,c,h,f){var g=0;var e=a.getElementById(h+g);while(e){var i=a.getElementById(c+g);var j=a.getElementById(d+g);if(i){if(i.value==null||i.value==""){i.value=f}else{e.checked="true";i.style.fontWeight="bold"}}g++;e=a.getElementById(h+g)}};b.bvClearTextbox=function(c,d){if(c&&c.value==d){c.value="";c.onfocus=null}};b.bvToggleTagBold=function(d,c){if(!c){return}if(d&&d.checked){c.style.fontWeight="bold";c.focus()}else{c.style.fontWeight="normal"}};b.bvTypedUserTag=function(e,c,d){if(c&&c.value.length>0){c.style.fontWeight="bold";if(e){e.checked="true"}if(d){d.style.display="";d.style.visibility="visible"}}};b.bvDisableReturn=function(d,c){var e=null;if(!d){d=b.event}if(d.keyCode){e=d.keyCode}else{if(d.which){e=d.which}}if(e==13&&c){c()}return e!=13}});
$BV.Internal.define("prr/analyticsHooksWrapperRR",[window],[],function(a){a.bvrrAnalyticsWrapper=function(b,c){if(typeof BVAnalyticsTracker==="object"&&BVAnalyticsTracker&&BVAnalyticsTracker.fireActionEvent){BVAnalyticsTracker.fireActionEvent(b,c)}}});