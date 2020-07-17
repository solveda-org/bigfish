
function dropdown_menu_hack(el) {
    if(el.runtimeStyle.behavior.toLowerCase()=="none"){return;}
    el.runtimeStyle.behavior="none";
    var ie5 = (document.namespaces==null);

    el.ondblclick = function(e) {
      window.event.returnValue=false;
      return false;
    }

    if(window.createPopup==null) {
        var fid = "dropdown_menu_hack_" + Date.parse(new Date());
        window.createPopup = function() {
            if(window.createPopup.frameWindow==null) {
                el.insertAdjacentHTML("AfterEnd","<iframe id='"+fid+"' name='"+fid+"' src='about:blank' frameborder='1' scrolling='no'></></iframe>");
                var f = document.frames[fid];
                f.document.open();
                f.document.write("<html><body></body></html>");
                f.document.close();
                f.fid = fid;
                var fwin = document.getElementById(fid);
                fwin.style.cssText="position:absolute;top:0;left:0;display:none;z-index:99999;";
                f.show = function(px,py,pw,ph,baseElement) {
                    py = py + baseElement.getBoundingClientRect().top + Math.max( document.body.scrollTop, document.documentElement.scrollTop) ;
                    px = px + baseElement.getBoundingClientRect().left + Math.max( document.body.scrollLeft, document.documentElement.scrollLeft) ;
                    fwin.style.width = pw + "px";
                    fwin.style.height = ph + "px";
                    fwin.style.posLeft =px ;
                    fwin.style.posTop = py ;
                    fwin.style.display="block";
                }

                f_hide = function(e) {
                    if(window.event && window.event.srcElement && window.event.srcElement.tagName && window.event.srcElement.tagName.toLowerCase()=="select"){return true;}
                    fwin.style.display="none";
                }
                f.hide = f_hide;
                document.attachEvent("onclick",f_hide);
                document.attachEvent("onkeydown",f_hide);
            }
            return f;
        }
        
    }

    function showMenu() {

        function selectMenu(obj) {
            var o = document.createElement("option");
            o.value = el.contentOptions[obj.selectedIndex].value;
            o.innerHTML = obj.innerHTML;
            while(el.options.length>0){el.options[0].removeNode(true);}
            el.appendChild(o);
            el.title = o.innerHTML;
            el.contentIndex = obj.selectedIndex ;
            el.menu.hide();
            if ($('cartShippingOptions')) {
                $('cartShippingOptions').submit();
            }
        }

        el.menu.show(0 , el.offsetHeight , 10, 10, el);
        var mb = el.menu.document.body;
        
        mb.style.cssText ="border:solid 1px black;margin:0;padding:0;overflow-y:auto;overflow-x:auto;background:white;text-aligbn:center;font-family:Verdana;font-size:12px;";
        var t = el.contentHTML;
        t = t.replace(/<select/gi,'<ul');
        t = t.replace(/<option/gi,'<li');
        t = t.replace(/<\/option/gi,'</li');
        t = t.replace(/<\/select/gi,'</ul');
        mb.innerHTML = t;

        el.select = mb.all.tags("ul")[0];
        el.select.style.cssText="list-style:none;margin:0;padding:0;";
        mb.options = el.select.getElementsByTagName("li");

        for(var i=0;i<mb.options.length;i++) {
            mb.options[i].selectedIndex = i;
            mb.options[i].style.cssText = "list-style:none;margin:0;padding:1px 2px;width/**/:100%;cursor:hand;cursorointer;white-space:nowrap;"
            mb.options[i].title =mb.options[i].innerHTML;
            mb.options[i].innerHTML ="<nobr>" + mb.options[i].innerHTML + "</nobr>";
            mb.options[i].onmouseover = function() {
                if( mb.options.selected ){mb.options.selected.style.background="white";mb.options.selected.style.color="black";}
                mb.options.selected = this;
                this.style.background="#333366";this.style.color="white";
            }
            mb.options[i].onmouseout = function(){this.style.background="white";this.style.color="black";}
            mb.options[i].onmousedown = function(){selectMenu(this); }
            mb.options[i].onkeydown = function(){selectMenu(this); }
            
        
            if(i == el.contentIndex) {
                mb.options[i].style.background="#333366";
                mb.options[i].style.color="white";
                mb.options.selected = mb.options[i];    
            }
        }
    
        var mw = Math.max( ( el.select.offsetWidth + 22 ), el.offsetWidth + 22 );
        mw = Math.max( mw, ( mb.scrollWidth+22) );
        var mh = mb.options.length * 15 + 8 ;
        
        var mx = (ie5)?-3:0;
        var my = el.offsetHeight -2;
        var docH = document.documentElement.offsetHeight ;
        var bottomH = docH - el.getBoundingClientRect().bottom ;
        
        mh = Math.min(mh, Math.max(( docH - el.getBoundingClientRect().top - 50),100) );

        if (( bottomH < mh) ) {
            mh = Math.max( (bottomH - 12),10);
            if ( mh <100 ) {
                my = -100 ;
            }
           mh = Math.max(mh,100);
        }

        self.focus();
        el.menu.show( mx , my , mw, mh , el);
        sync=null;
        if (mb.options.selected) {
            mb.scrollTop = mb.options.selected.offsetTop;
        }

        window.onresize = function(){el.menu.hide()};
    }
    
    function switchMenu() {
        if (event.keyCode) {
            if(event.keyCode==40){ 
                el.contentIndex++ ;
            } else if(event.keyCode==38) {
                el.contentIndex--; 
            }
        } else if(event.wheelDelta ) {
            if (event.wheelDelta >= 120)
                el.contentIndex++ ;
            else if (event.wheelDelta <= -120)
                el.contentIndex-- ;
        } else {return true;}
    
        if ( el.contentIndex > (el.contentOptions.length-1) ){ el.contentIndex =0;}
        else if (el.contentIndex<0){el.contentIndex = el.contentOptions.length-1 ;}
        
        var o = document.createElement("option");
        o.value = el.contentOptions[el.contentIndex].value;
        o.innerHTML = el.contentOptions[el.contentIndex].text;
        while(el.options.length>0){el.options[0].removeNode(true);}
        el.appendChild(o);
        el.title = o.innerHTML;
    }

    if(dropdown_menu_hack.menu ==null) {
       dropdown_menu_hack.menu = window.createPopup();
       document.attachEvent("onkeydown",dropdown_menu_hack.menu.hide);
    }
    el.menu = dropdown_menu_hack.menu ;
    el.contentOptions = new Array();
    el.contentIndex = el.selectedIndex;
    el.contentHTML = el.outerHTML;
    
    for(var i=0;i<el.options.length;i++) {
        el.contentOptions [el.contentOptions.length] = 
        {
           "value": el.options[i].value,
           "text": el.options[i].innerHTML
        }
       if(!el.options[i].selected){el.options[i].removeNode(true);i--;};
    }
    el.onkeydown = switchMenu;
    el.onclick = showMenu;
    el.onmousewheel= switchMenu;
}
