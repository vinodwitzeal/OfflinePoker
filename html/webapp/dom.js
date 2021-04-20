var baseUrl="https://test1.bigcash.live/bulbsmashpro/api/";

  function startMain(){
      window.setGameScreen();
  }


class Label{
    constructor(text,size){
        var div=document.createElement("div");
        div.innerHTML=text;
        div.style.fontSize=size+"px";
        this.child=div;
    }

    setText(text){
        this.child.innerHTML=text;
    }
}
class Row{
    constructor(){
        this.row=document.createElement("tr");
        this.row.style.width="100%";
    }

    add(column){
        this.row.appendChild(column.column);
    }
}

class Column{
    constructor(){
        this.column=document.createElement("td");
    }

    add(child){
        this.child=child;
        this.column.appendChild(child);
        return this;
    }

    colspan(colspan){
        this.column.setAttribute("colspan",colspan);
        return this;
    }

    width(width){
        this.child.style.width=width;
        return this;
    }

    height(height){
        this.child.style.height=height;
        return this;
    }

    horizontalAlign(align){
        this.column.setAttribute("align",align);
        return this;
    }

    verticalAlign(align){
        this.column.setAttribute("valign",align);
        return this;
    }

    expandX(){
        this.child.style.width="100%";
        return this;
    }

    fillX(){
        this.child.style.width="100%";
        return this;
    }

    expandY(){
        this.column.style.height="100%";
        return this;
    }


    fillY(){
        this.child.style.height="100%";
        return this;
    }

    expand(){
        this.expandX();
        this.expandY();
    }

    fill(){
        this.fillX();
        this.fillY();
    }

}


class ModelTable{
    constructor(){
        this.table=document.createElement("table");
        this.table.style.fontFamily="Arial, Helvetica, sans-serif";
    }

    setBackground(background){
        this.table.style.backgroundImage=background;
    }

    setBackgroundColor(color){
        this.table.style.background=color;
    }

    setSize(width,height){
        this.table.style.width=width;
        this.table.style.height=height;
    }


    row(){
        this.currentRow=new Row();
        this.table.appendChild(this.currentRow.row);
    }

    

    add(child){
        if(this.currentRow==null){
            this.row();
        }
        var column=new Column();
        this.currentRow.add(column);
        column.add(child);
        return column;
    }

    clear(){
        while(this.table.firstChild){
            this.table.removeChild(this.table.firstChild);
        }
    }
}

class ModelForm extends ModelTable{
    constructor(){
        super();
        this.table.style.borderCollapse="separate";
        this.table.style.borderSpacing="0 4px";
    }

    start(){
        // var div=document.createElement("div");
        // var columnChild=super.add(div).width("4px").column;
        // columnChild.style.width="4px";
        // columnChild.style.border="2px";
        // columnChild.style.borderRadius="2px";
        // columnChild.style.borderColor="#626262";
        // columnChild.style.borderStyle="solid none solid solid";
        // columnChild.style.paddingTop="4px";
        // columnChild.style.paddingBottom="4px";
    }

    end(){
        // var div=document.createElement("div");
        // var columnChild=super.add(div).width("4px").column;
        // columnChild.style.width="4px";
        // columnChild.style.border="2px";
        // columnChild.style.borderRadius="2px";
        // columnChild.style.borderColor="#626262";
        // columnChild.style.borderStyle="solid solid solid none";
        // columnChild.style.paddingTop="4px";
        // columnChild.style.paddingBottom="4px";
    }

    add(child){
        var column=super.add(child);
        // column.column.style.border="2px";
        // column.column.style.borderColor="#626262";
        // column.column.style.borderStyle="solid none solid none";
        // column.column.style.paddingTop="4px";
        // column.column.style.paddingBottom="4px";
        return column;
    }

    row(){
        this.currentRow=new Row();
        this.table.appendChild(this.currentRow.row);
        var row=this.currentRow.row;
        row.style.width="4px";
        row.style.border="2px";
        row.style.borderRadius="2px";
        row.style.borderColor="#626262";
        row.style.borderStyle="solid";
        row.style.paddingTop="4px";
        row.style.paddingBottom="4px"
    }

}


var mainDocument=document.getElementById("html-splash");
var width=window.innerWidth;
var height=window.innerHeight;


class QRDialog{
    constructor(callback){
        this.callback=callback;
        var mainDiv=document.createElement("div");
        mainDiv.style.fontFamily="Arial, Helvetica, sans-serif";
        mainDiv.style.width="100%";
        mainDiv.style.height="100%";
        mainDiv.style.justifyContent="center";
        mainDiv.style.alignItems="center";
        mainDiv.style.backgroundColor="#000000dd";
        mainDiv.style.display="none";
        mainDiv.style.position="fixed";
        mainDiv.style.zIndex="1";
        mainDiv.style.left="0";
        mainDiv.style.top="0";
        this.scanningFromCamera=false;

        var contentTable=document.createElement("div");
        contentTable.style.display="flex";
        contentTable.style.flexFlow="column";
        contentTable.style.justifyContent="center";
        contentTable.style.alignItems="center";
        contentTable.style.borderRadius="4px";
        contentTable.style.backgroundColor="#ffffff";
        // contentTable.style.width="80%";

        var headerLabel=new Label("SCANNING QR",16);
        headerLabel.child.style.color="#000000";
        headerLabel.child.style.fontWeight="bolder";
        contentTable.appendChild(headerLabel.child);

        var errorLabel=new Label("",6);
        errorLabel.child.style.color="red";
        contentTable.appendChild(errorLabel.child);
        this.errorLabel=errorLabel;

        var qrDiv=document.createElement("div");
        qrDiv.style.display="block";
        qrDiv.id="menu-reader";
        qrDiv.style.width="250px";
        contentTable.appendChild(qrDiv);

        var buttonDiv=document.createElement("div");
        buttonDiv.style.display="flex";
        buttonDiv.style.flexFlow="row";
        buttonDiv.style.justifyContent="center";
        buttonDiv.style.alignItems="center";
        buttonDiv.style.width="100%";

        var scanButton=document.createElement("div");
        scanButton.innerHTML="SCAN WITH CAMERA";
        scanButton.style.display="none";
        scanButton.setAttribute("class","btn-scan");
        this.scanButton=scanButton;

        var _that=this;
        this.scanButton.addEventListener("click",function(){
            if(_that.scanningFromCamera){
                _that.hide();
            }else{

            }
        });

        var fileDiv=document.createElement("div");
        var fileUpload=document.createElement("file");
        fileUpload.setAttribute("accept","image/*");
        fileUpload.style.display="none";
        fileDiv.appendChild(fileUpload);
        var fileButton=document.createElement("div");
        fileButton.innerHTML="SCAN FROM FILE";
        fileButton.setAttribute("class","btn-submit");
        fileDiv.appendChild(fileButton);
        fileButton.addEventListener("click",function(){
            fileUpload.click();
        });

        this.fileDiv=fileDiv;


        buttonDiv.appendChild(scanButton);
        buttonDiv.appendChild(fileDiv);

        contentTable.appendChild(buttonDiv);

        mainDiv.appendChild(contentTable);

        this.mainDiv=mainDiv;
        
    }

    stopScanning(){
        if(this.qrscanner){
            this.qrscanner.stop();
        }
    }

    scanFromFile(fileName){
        if(!this.qrscanner){
            this.qrscanner=new Html5Qrcode("menu-reader");
        }
        var _that=this;
        this.qrscanner.scanFile(fileName,true)
            .then(function(qrMessage){
                try{
                    var messages=qrMessage.split("qr=");
                    if (messages.length==2) {
                        _that.callback(messages[1]);
                        _that.hide();
                    }else {
                        _that.errorLabel.setText("Invalid QR");
                    }
                }catch(error){

                }
                
            }).catch(function(qrError){
                _that.errorLabel.setText("QR Not Found");
            });
    }

    startScanning(){
        if(!this.qrscanner){
            this.qrscanner=new Html5Qrcode("menu-reader");
        }
        var _that=this;
        this.qrscanner.start(
            {facingMode:"environment"},
            {fps:100,qrbox:250},
            function(qrMessage){
                try{
                    var messages=qrMessage.split("qr=");
                    if (messages.length==2) {
                        _that.callback(messages[1]);
                        _that.hide();
                    }else {
                        _that.errorLabel.setText("Invalid QR");
                    }
                }catch(error){

                }
            },
            function(qrError){
                _that.errorLabel.setText("QR Not Found");
            }
        )
        .then(function(){
            _that.scanningFromCamera=true;
            _that.scanButton.innerHTML="STOP SCANNING";
            _that.scanButton.setAttribute("class","btn-stop");
            _that.scanButton.style.display="block";
        })
        .catch(function(error){

        });

        setTimeout(function(){
            _that.fileDiv.style.display="block";
        },3);
    }

    
    show(){
        mainDocument.appendChild(this.mainDiv);
        this.mainDiv.style.display="flex";
        this.mainDiv.focus();
        this.startScanning();    
    }

    hide(){
        this.stopScanning();
        mainDocument.removeChild(this.mainDiv);
        this.mainDiv.style.display="none";
        this.mainDiv.blur();
    }


}

class ImageDiv{
    constructor(width,height,url){
        var imageDiv=document.createElement("div");
        imageDiv.style.width=width+"px";
        imageDiv.style.height=height+"px";
        imageDiv.style.position="absolute";
        imageDiv.style.overflow="hidden";
        
        var image=document.createElement("img");
        image.style.width=width+"px";
        image.style.height=height+"px";
        image.style.overflow="auto";
        // image.setAttribute("width",width+"");
        // image.setAttribute("height",height+"");
        image.src=url;

        this.width=width;
        this.height=height;

        this.maxWidth=2*width;
        this.maxHeight=2*height;

        this.stepWidth=width/10;
        this.stepHeight=height/10;

        imageDiv.appendChild(image);

        this.imageDiv=imageDiv;
        this.image=image;
    }


    hide(){
        console.log("Hide Page");
        this.imageDiv.style.display="none";
        this.image.style.width=this.width;
        this.image.style.height=this.height;
    }

    resetPage(){
        this.imageDiv.style.display="block";
        this.image.style.width=this.width+"px";
        this.image.style.height=this.height+"px";
    }

    zoomIn(){
        var clientWidth=this.image.clientWidth;
        var clientHeight=this.image.clientHeight;

        if(clientWidth<this.maxWidth && clientHeight<this.maxHeight){
            clientWidth=clientWidth+this.stepWidth;
            clientHeight=clientHeight+this.stepHeight;
        }

        this.image.style.width=clientWidth+"px";
        this.image.style.height=clientHeight+"px";
    }


    zoomOut(){
        var clientWidth=this.image.clientWidth;
        var clientHeight=this.image.clientHeight;

        if(clientWidth>this.width && clientHeight>this.width){
            clientWidth=clientWidth-this.stepWidth;
            clientHeight=clientHeight-this.stepHeight;
        }

        this.image.style.width=clientWidth+"px";
        this.image.style.height=clientHeight+"px";
    }
}

class StackDiv{
    constructor(direction){
        var stackDiv=document.createElement("div");
        stackDiv.style.width="100%";
        stackDiv.style.height="100%";
        stackDiv.style.position="absolute";
        stackDiv.style.display="flex";
        stackDiv.style.flexFlow=direction;
        this.stackDiv=stackDiv;
    }
}

class MenuDialog{
    constructor(width,height){
        var mainDiv=document.createElement("div");
        mainDiv.style.fontFamily="Arial, Helvetica, sans-serif";
        mainDiv.style.width="100%";
        mainDiv.style.height="100%";
        mainDiv.style.justifyContent="center";
        mainDiv.style.alignItems="center";
        mainDiv.style.backgroundColor="#000000dd";
        mainDiv.style.display="none";
        mainDiv.style.position="fixed";
        mainDiv.style.zIndex="1";
        mainDiv.style.left="0";
        mainDiv.style.top="0";

        var contentTable=document.createElement("div");
        contentTable.style.display="flex";
        contentTable.style.flexFlow="column";
        contentTable.style.justifyContent="center";
        contentTable.style.alignItems="center";
        contentTable.style.borderRadius="4px";
        contentTable.style.backgroundColor="#ffffff";
        contentTable.style.width=width+"px";
        contentTable.style.height=height+"px";



        var image4=new ImageDiv(width,height,"https://pocket-syndicated-images.s3.amazonaws.com/5f4d569ebaa8d.jpg");
        var image3=new ImageDiv(width,height,"https://pocket-syndicated-images.s3.amazonaws.com/5e9720121f721.jpg");
        var image2=new ImageDiv(width,height,"https://pocket-syndicated-images.s3.amazonaws.com/5e9720121f721.jpg");
        var image1=new ImageDiv(width,height,"https://pocket-syndicated-images.s3.amazonaws.com/5f4d569ebaa8d.jpg");

        this.pages=[image4,image3,image2,image1];
        this.currentIndex=0;

        contentTable.appendChild(image1.imageDiv);
        contentTable.appendChild(image2.imageDiv);
        contentTable.appendChild(image3.imageDiv);
        contentTable.appendChild(image4.imageDiv);

        var buttonDiv=document.createElement("div");
        buttonDiv.style.display="flex";
        buttonDiv.style.width=width+"px";
        buttonDiv.style.height=height+"px";
        buttonDiv.style.position="absolute";
        buttonDiv.style.flexWrap="wrap";
        buttonDiv.style.alignContent="space-between";


        var closeDiv=document.createElement("div");
        closeDiv.style.width=width+"px";
        closeDiv.style.padding="4px";
        var closeImage=document.createElement("img");
        closeImage.src="img/menu_cross.png";
        closeImage.style.width="10%";
        closeImage.style.height="auto";
        closeImage.style.display="block"
        closeImage.style.position="absolute";
        closeImage.style.right="0";
        closeImage.style.top="0";
        closeDiv.appendChild(closeImage);

        buttonDiv.appendChild(closeDiv);

        var controlDiv=document.createElement("div");
        controlDiv.style.width=width+"px";
        controlDiv.style.padding="4px";
        var previousImage=document.createElement("img");
        previousImage.src="img/menu_previous.png";
        previousImage.style.width="10%";
        previousImage.style.height="auto";
        previousImage.style.display="block";
        previousImage.style.position="absolute";
        previousImage.style.left="0";
        previousImage.style.display="none";
        controlDiv.appendChild(previousImage);
        var nextImage=document.createElement("img");
        nextImage.src="img/menu_next.png";
        nextImage.style.width="10%";
        nextImage.style.height="auto";
        nextImage.style.position="absolute";
        nextImage.style.right="0";
        
        controlDiv.appendChild(nextImage);

        buttonDiv.appendChild(controlDiv);

        var zoomDiv=document.createElement("div");
        zoomDiv.style.width=width+"px";
        zoomDiv.style.display="flex";
        zoomDiv.style.padding="4px";
        zoomDiv.style.justifyContent="center";
        zoomDiv.style.alignItems="center";
        var zoomOutImage=document.createElement("img");
        zoomOutImage.src="img/menu_zoomout.png";
        zoomOutImage.style.width="10%";
        zoomOutImage.style.height="auto";
        zoomDiv.appendChild(zoomOutImage);
        var zoomInImage=document.createElement("img");
        zoomInImage.src="img/menu_zoomin.png";
        zoomInImage.style.width="10%";
        zoomInImage.style.height="auto";
        zoomDiv.appendChild(zoomInImage);

        buttonDiv.appendChild(zoomDiv);
       
        contentTable.appendChild(buttonDiv);


        var pageDiv=document.createElement("div");
        pageDiv.style.backgroundColor="#00000088";
        pageDiv.style.borderRadius="8px";
        pageDiv.style.fontSize="10px";
        pageDiv.style.color="white";
        pageDiv.style.position="absolute";
        pageDiv.style.display="flex";
        pageDiv.style.flexWrap="wrap";
        pageDiv.style.right="0";
        pageDiv.style.padding="4px";
        pageDiv.style.bottom="0";

        pageDiv.innerHTML="1/"+this.pages.length;

        this.pageDiv=pageDiv;
    

        contentTable.appendChild(pageDiv);


        console.log(this.pages);
        closeImage.addEventListener("click",function(){
            _that.hide();
        });

        var _that=this;

        previousImage.addEventListener("click",function(){
            if (_that.currentIndex <= 0) return;
                var index = _that.currentIndex - 1;
                var currentPage = _that.pages[index];
                currentPage.resetPage();
                _that.currentIndex = index;
                _that.setPageNumber(_that.currentIndex + 1);
                if (_that.currentIndex<=0){
                    _that.hideButton(previousImage);
            
                }
                if (_that.currentIndex<_that.pages.length-1){
                    _that.showButton(nextImage);
                }
        });

        nextImage.addEventListener("click",function(){
            if (_that.currentIndex >= (_that.pages.length - 1)) return;
                var index = _that.currentIndex + 1;
                var previousPage = _that.pages[_that.currentIndex];
                previousPage.hide();
                var  currentPage =_that.pages[index];
                currentPage.resetPage();
                _that.currentIndex = index;
                _that.setPageNumber(_that.currentIndex + 1);
                if (_that.currentIndex>=(_that.pages.length-1)){
                    _that.hideButton(nextImage);
                }
                if (_that.currentIndex>0){
                   _that.showButton(previousImage);
                }
        });

        zoomOutImage.addEventListener("click",function(){
            var currentPage=_that.pages[_that.currentIndex];
            currentPage.zoomOut();
        })


        zoomInImage.addEventListener("click",function(){
            var currentPage=_that.pages[_that.currentIndex];
            currentPage.zoomIn();
        })






        mainDiv.appendChild(contentTable);


        this.mainDiv=mainDiv;

    }

    hideButton(image){
        image.style.display="none";
    }

    showButton(image){
        image.style.display="block";
    }


    setPageNumber(count){
        this.pageDiv.innerHTML=count+"/"+this.pages.length;
    }

    show(){
        mainDocument.appendChild(this.mainDiv);
        this.mainDiv.style.display="flex";
        this.mainDiv.focus();
    }

    hide(){
        mainDocument.removeChild(this.mainDiv);
        this.mainDiv.style.display="none";
        this.mainDiv.blur();
    }
}



class ProcessDialog{
    constructor(){
        var mainDiv=document.createElement("div");
       mainDiv.style.fontFamily="Arial, Helvetica, sans-serif";
       mainDiv.style.width="100%";
       mainDiv.style.height="100%";
       mainDiv.style.justifyContent="center";
       mainDiv.style.alignItems="center";
       mainDiv.style.backgroundColor="#000000dd";
       mainDiv.style.display="none";
       mainDiv.style.position="fixed";
       mainDiv.style.zIndex="1";
       mainDiv.style.left="0";
       mainDiv.style.top="0";

       var contentTable=document.createElement("div");
       contentTable.style.display="flex";
       contentTable.style.flexFlow="column";
       contentTable.style.justifyContent="center";
       contentTable.style.alignItems="center";
       contentTable.style.borderRadius="4px";
       
       var processImage=document.createElement("img");
       processImage.src="img/process.gif";
       processImage.style.height="60px";
       processImage.style.width="60px";
       contentTable.appendChild(processImage);
       var label=new Label("Please Wait...","10px");
       label.child.style.color="#ffffff";
       contentTable.appendChild(label.child);

       mainDiv.appendChild(contentTable);

       this.mainDiv=mainDiv;

    }


    show(){
        mainDocument.appendChild(this.mainDiv);
        this.mainDiv.style.display="flex";
        this.mainDiv.focus();
    }

    hide(){
        mainDocument.removeChild(this.mainDiv);
        this.mainDiv.style.display="none";
        this.mainDiv.blur();
    }


}

class ModelDialog{
    constructor(){
       var mainDiv=document.createElement("div");
       mainDiv.style.fontFamily="Arial, Helvetica, sans-serif";
       mainDiv.style.width="100%";
       mainDiv.style.height="100%";
       mainDiv.style.justifyContent="center";
       mainDiv.style.alignItems="center";
       mainDiv.style.backgroundColor="#000000dd";
       mainDiv.style.display="none";
       mainDiv.style.position="fixed";
       mainDiv.style.zIndex="1";
       mainDiv.style.left="0";
       mainDiv.style.top="0";
       mainDiv.style.overflow="auto";
       var contentTable=document.createElement("div");
       contentTable.style.display="flex";
       contentTable.style.flexFlow="column";
       contentTable.style.justifyContent="center";
       contentTable.style.alignItems="center";
       contentTable.style.backgroundColor="#ffffff";
       contentTable.style.borderRadius="4px";


       var buttonTable=document.createElement("div");
       buttonTable.style.display="flex";
       buttonTable.style.width="100%";
       buttonTable.style.justifyContent="center";
       buttonTable.style.alignItems="center";
       buttonTable.style.backgroundColor="#eeeeee";
       buttonTable.style.borderRadius="0px 0px 4px 4px";
       buttonTable.style.marginTop="8px";


       var button=document.createElement("button");
       button.style.width="80%";
       button.style.height="auto";
       button.style.textAlign="center";
       button.style.paddingTop="4px";
       button.style.paddingBottom="4px";
       button.style.marginTop="8px";
       button.style.marginBottom="8px";
       button.style.backgroundImage="linear-gradient(#4ba614,#018c00)";
       button.style.outline="none";
       button.style.borderRadius="4px";
       button.style.fontSize="18px";
       button.style.color="#ffffff";
       button.style.fontWeight="bold";
       button.style.border="none";
       buttonTable.appendChild(button);


       this.mainDiv=mainDiv;
       this.contentTable=contentTable;
       this.buttonTable=buttonTable;
       this.mainDiv.appendChild(this.contentTable);
       this.button=button;
    }

    addButton(){
        this.contentTable.appendChild(this.buttonTable);
    }



    show(){
        mainDocument.appendChild(this.mainDiv);
        this.mainDiv.style.display="flex";
        this.mainDiv.focus();
    }

    hide(){
        mainDocument.removeChild(this.mainDiv);
        this.mainDiv.style.display="none";
        this.mainDiv.blur();
    }
}

class CrossIcon{
    constructor(){
        var closeDiv=document.createElement("div");
        closeDiv.style.display="flex";
        closeDiv.style.width="100%";
        closeDiv.style.paddingRight="4px";
        closeDiv.style.flexDirection="row-reverse";
        closeDiv.style.fontSize="25px";
        closeDiv.style.fontWeight="bold";
        closeDiv.style.cursor="ponter";
        var closeText=document.createElement("span");
        closeText.innerHTML="&times;";
        closeText.style.float="right";
        closeDiv.appendChild(closeText);
        this.child=closeDiv;
    }
}


class TextBox{
    constructor(imgIcon,placeholder,type){

        var iconDiv=document.createElement("div");
        var icon=document.createElement("img");
        icon.src=imgIcon;
        this.icon=icon;
        icon.style.height="12px";
        icon.style.marginLeft="5px";
        iconDiv.appendChild(icon);

        var seperator=document.createElement("div");
        seperator.style.width="1px";
        seperator.style.height="16px";
        seperator.style.backgroundColor="#6e6e6e";
        seperator.style.marginLeft="5px";
        seperator.style.marginRight="5px";
        this.seperator=seperator;

        var textBox=document.createElement("input");
        textBox.setAttribute("type",type);
        textBox.setAttribute("placeholder",placeholder);
        textBox.setAttribute("required","true");
        textBox.style.fontSize="12px";
        textBox.style.outline="none";
        textBox.style.border="none";
        textBox.style.width="100%";
        textBox.style.height="auto";


        this.textBox=textBox;


        var inputDiv=document.createElement("div");
        inputDiv.style.display="flex";
        inputDiv.style.flexFlow="row";
        inputDiv.style.border="2px solid #d2d2d2";
        inputDiv.style.borderRadius="4px";
        inputDiv.style.padding="4px";
        inputDiv.style.width="80%";
        inputDiv.style.justifyContent="center";
        inputDiv.style.alignItems="center";
        inputDiv.style.marginBottom="5px";

        inputDiv.appendChild(iconDiv);
        inputDiv.appendChild(seperator);
        inputDiv.appendChild(textBox);

        this.inputDiv=inputDiv;

    }

    getText(){
        return this.textBox.value;
    }

    isValid(){
        return this.textBox.checkValidity();
    }

}

class PasswordBox{
    constructor(placeholder){
        var iconDiv=document.createElement("div");
        var icon=document.createElement("img");
        icon.src="img/password.png";
        this.icon=icon;
        icon.style.height="12px";
        icon.style.marginLeft="8px";
        icon.style.marginRight="3px";
        iconDiv.appendChild(icon);

        var seperator=document.createElement("div");
        seperator.style.width="1px";
        seperator.style.height="16px";
        seperator.style.backgroundColor="#6e6e6e";
        seperator.style.marginLeft="5px";
        seperator.style.marginRight="5px";
        this.seperator=seperator;

        var textBox=document.createElement("input");
        textBox.setAttribute("type","password");
        textBox.setAttribute("placeholder",placeholder);
        textBox.setAttribute("required","true");
        textBox.style.fontSize="12px";
        textBox.style.outline="none";
        textBox.style.border="none";
        textBox.style.width="100%";
        textBox.style.height="auto";


        this.textBox=textBox;


        var inputDiv=document.createElement("div");
        inputDiv.style.display="flex";
        inputDiv.style.flexFlow="row";
        inputDiv.style.border="2px solid #d2d2d2";
        inputDiv.style.borderRadius="4px";
        inputDiv.style.padding="4px";
        inputDiv.style.width="80%";
        inputDiv.style.justifyContent="center";
        inputDiv.style.alignItems="center";
        inputDiv.style.marginBottom="5px";

        inputDiv.appendChild(iconDiv);
        inputDiv.appendChild(seperator);
        inputDiv.appendChild(textBox);


        var passwordHidden=true;
        var passwordDiv=document.createElement("div");
        var passwordImage=document.createElement("img");
        passwordImage.src="img/password_hidden.png";
        passwordImage.style.height="12px";
        passwordDiv.appendChild(passwordImage);

        passwordImage.addEventListener("click",function(){
            if(passwordHidden){
                passwordImage.src="img/password_visible.png";
                textBox.setAttribute("type","text")
                passwordHidden=false;
            }else{
                passwordImage.src="img/password_hidden.png";
                textBox.setAttribute("type","password")
                passwordHidden=true;
            }
        })

        passwordImage=passwordImage;
        inputDiv.appendChild(passwordDiv);
        this.inputDiv=inputDiv;
    }

    getText(){
        return this.textBox.value;
    }

    
    isValid(){
        return this.textBox.checkValidity();
    }
}

class CheckBox{
    constructor(){
        var div=document.createElement("div");
        div.setAttribute("class","terms");
        
        div.appendChild(input);
        div.appendChild(span);

        this.child=div;
    }
}

class TermsLabel{
    constructor(linkstyle){
        var label=document.createElement("label");
        label.setAttribute("class","terms");
        label.innerHTML="By signing up, you accept you are <i><b>18+</b></i> and agree to our <i><b><a href=\"https://bigcash.live/terms.html\" target=\"_blank\" class=\""+linkstyle+"\">Terms and Conditions.</a></b></i>";

        var input=document.createElement("input");
        input.setAttribute("type","checkbox");
        input.checked=true;
        var span=document.createElement("span");
        span.setAttribute("class","checkmark");

        label.appendChild(input);
        label.appendChild(span);

        this.child=label;
        this.input=input;

        var errorLabel=document.createElement("label");
        errorLabel.style.backgroundColor="#00000088";
        errorLabel.style.borderRadius="8px";
        errorLabel.style.padding="6px";
        errorLabel.style.fontSize="10px";
        errorLabel.style.color="white";
        errorLabel.style.display="none";
        errorLabel.textContent="You must accept terms and conditions to sign in to your account.";

        input.addEventListener("change",function(){
            if(input.checked){
                errorLabel.style.display="none";
            }
        });

        this.errorLabel=errorLabel;
    }

    isChecked(){
        return this.input.checked;
    }

    showError(){
        this.errorLabel.style.display="block";
    }
}

function callPost(url,params,callback){

    const qs = Object.keys(params)
    .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
    .join('&');


    var request=new XMLHttpRequest();

    request.open("POST",url);
    request.setRequestHeader("content-type","application/x-www-form-urlencoded");

    request.onload=function(){
        callback(request.status,request.responseText);
    }
    
    request.send(qs);

}

function callPostWithBody(url,params,callback){
    var data=JSON.stringify(params);
    console.log(data);
    var request=new XMLHttpRequest();
    // request.withCredentials=true;
    request.onload=function(){
        callback(request.status,request.responseText);
    }
    request.open("POST",url);
    request.setRequestHeader("Content-Type","application/json; utf-8");
    request.send(data);
}

function  getQRInfo(params) {
    const qs = Object.keys(params)
    .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
    .join('&');


    var request=new XMLHttpRequest();

    request.open("POST",baseUrl+"v1/qr/getinfo");
    request.setRequestHeader("content-type","application/x-www-form-urlencoded");

    request.onload=function(){
        callback(request.status,request.responseText);
    }
    
    request.send(qs);
}


function parseCookie(cookieData,processPop){
    var otp=cookieData.otp;
    var userId=cookieData.userId;
    callAppLaunch(otp,userId,cookieData,processPop);
}

function encryptValue(otp,transcationId,value){
    console.log("OTP:"+otp+",TRANSACTION-ID:"+transcationId+",VALUE:"+value);
    var otSub=otp.substring(0,3);
    console.log(otSub);
    var key=otp.substring(0,3)+transcationId;
    console.log(key);
    console.log("otp:"+otp);
    return window.encrypt(value,key);
}

function callAppLaunch(otp,userId,cookieData,processPop){
    var transactionId=new Date().getTime();

    var params={
        "otp":encryptValue(otp,transactionId,otp),
        "transactionId":transactionId+"",
        "deviceType":"ANDROID",
        "appVersion":"93",
        "deviceId":window.deviceId,
        "androidVersion":"8.1.0",
        "isOpen":"true",
        "networkType":"VI",
        "isRooted":"false",
        "isHavingCloakingApps":"false",
        "multiWindowEnabled":"false",
        "configUpdatedTime":"0",
        "latitude":"",
        "longitude":"",
        "parentActivity":"",
        "sId":"93",
        "rbs":"false",
        "p":"/data/user/0/best.bulbsmash.cash/files",
        "cp":"/data/data/best.bulbsmash.cash/files"
    }

    callPost(baseUrl+"v10/user/appLaunch?userId="+userId,params,function(state,response){
        if(state==200){
            window.cookieData=cookieData;
            window.appLaunchResponse=response;
            startMain();
        }else{
            processPop.hide();
            window.clearGameCookie();
        }
    });
}

function callGoogleLogin(authType,accessToken,processPop){
    var params={
        "authType":authType,
        "deviceType":"ANDROID",
        "appVersion":"93",
        "deviceId":window.deviceId,
        "androidId":window.androidId,
        "androidVersion":"8.1.0",
        "networkOperator":"VI",
        "isRooted":"false",
        "referralCode":"",
        "aName":window.appName,
        "pName":window.packageName,
        "kH":window.keyHash,
        "p":"/data/user/0/best.bulbsmash.cash/files",
        "cp":"/data/data/best.bulbsmash.cash/files",
        "utmSource":"",
        "utmMedium":"",
        "utmContent":"",
        "utmTerm":"",
        "utmCampaign":"",
        "downloadLink":"",
        "sId":"7eNuF20uEhbdi6IQ3fB3lg==",
        "dn":"7eNuF20uEhbdi6IQ3fB3lg==",
        "rbs":"false",
        "googleLoginDTO":{
            "accessToken":accessToken
        }
    }

    callPostWithBody(baseUrl+"v6/user/login",params,function(state,response){
        console.log(state);
        console.log(response);
        if(state==200){
            var cookieData=window.setGameCookie(response);
            parseCookie(cookieData,processPop);
        }else{
            processPop.hide();
        }
    });
}

function callFBLogin(accessToken,processPop){
    var params={
        "deviceType":"ANDROID",
        "appVersion":"93",
        "deviceId":window.deviceId,
        "androidId":window.androidId,
        "androidVersion":"8.1.0",
        "networkOperator":"VI",
        "isRooted":"false",
        "accessToken":accessToken,
        "referralCode":"",
        "aName":window.appName,
        "pName":window.packageName,
        "kH":window.keyHash,
        "p":"/data/user/0/best.bulbsmash.cash/files",
        "cp":"/data/data/best.bulbsmash.cash/files",
        "utmSource":"",
        "utmMedium":"",
        "utmContent":"",
        "utmTerm":"",
        "utmCampaign":"",
        "downloadLink":"",
        "sId":"7eNuF20uEhbdi6IQ3fB3lg==",
        "dn":"7eNuF20uEhbdi6IQ3fB3lg==",
        "rbs":"false"
    }
    
    callPost(baseUrl+"v5/user/fbLogin",params,function(state,response){
        console.log(state);
        console.log(response);

        if(state==200){
            var cookieData=window.setGameCookie(response);
            parseCookie(cookieData,processPop);
        }else{
            processPop.hide();
        }
    });

}


class RegisterDialog extends ModelDialog{

    constructor(width,height){
        super();
        var _that=this;
        var dialogWidth=width*4/5;
        this.contentTable.style.width=dialogWidth+"px";
        

        var crossButton=new CrossIcon();
        crossButton.child.addEventListener("click",function(){
            _that.hide();
        });

        this.contentTable.appendChild(crossButton.child);

        var logoImage=document.createElement("img");
        logoImage.src="img/lock.png";
        logoImage.style.width="80%";
        logoImage.style.height="auto";
        logoImage.style.display="block";
        this.contentTable.appendChild(logoImage);

        var titleLabel=new Label("Register",18);
        titleLabel.child.style.fontWeight="bold";
        titleLabel.child.style.marginBottom="10px";
        this.contentTable.appendChild(titleLabel.child);

        var nameBox=new TextBox("img/user.png","Enter your name","text");
        nameBox.textBox.setAttribute("pattern","[A-Za-z]+");
       
        var emailBox=new TextBox("img/email.png","Enter your email","email");
    
        var passwordBox=new PasswordBox("Enter your password");

        var confirmPasswordBox=new PasswordBox("Confirm your password");

        this.contentTable.appendChild(nameBox.inputDiv);
        this.contentTable.appendChild(emailBox.inputDiv);
        this.contentTable.appendChild(passwordBox.inputDiv);
        this.contentTable.appendChild(confirmPasswordBox.inputDiv);
        
        var errorLabel=new Label("",8);
        errorLabel.child.style.color="red";
        errorLabel.child.style.width="80%";
        errorLabel.child.style.textAlign="center";
        this.contentTable.appendChild(errorLabel.child);

        var termLabel=new Label("By registering you accept you are <span class=\"register-link\">18+</span> and agree to our <a href=\"https://bigcash.live/terms.html\" target=\"_blank\" class=\"register-link\">*T&C.</a>",8);
        termLabel.child.style.color="black";
        termLabel.child.style.marginTop="10px";
        termLabel.child.style.textAlign="center";
        termLabel.child.style.width="80%";
        this.contentTable.appendChild(termLabel.child);


        this.button.innerHTML="Register";

        var registerButton=this.button;

        this.addButton();

        registerButton.addEventListener("click",function(){
            if(!nameBox.isValid()){
                errorLabel.setText("Name can contain only letters.");
                return;
            }

            if(!emailBox.isValid()){
                errorLabel.setText("Enter Valid Email.");
                return;
            }

            if(!passwordBox.isValid()){
                errorLabel.setText("Enter Password");
                return;
            }

            if(!confirmPasswordBox.isValid()){
                errorLabel.setText("Confirm Password");
                return;
            }


            if(passwordBox.getText()!=confirmPasswordBox.getText()){
                errorLabel.setText("Password do not match");
                return;
            }

            var params={
                "name":nameBox.getText(),
                "emailId":emailBox.getText(),
                "password":passwordBox.getText(),
                "deviceType":"ANDROID",
                "deviceId":window.deviceId,
                "appVersion":"93",
                "androidId":window.androidId,
                "androidVersion":"8.1.0",
                "networkOperator":"VI",
                "isRooted":"false",
                "referralCode":"",
                "aName":window.appName,
                "pName":window.packageName,
                "kH":window.keyHash,
                "p":"/data/user/0/best.bulbsmash.cash/files",
                "cp":"/data/data/best.bulbsmash.cash/files",
                "rbs":"false",
                "sId":"7eNuF20uEhbdi6IQ3fB3lg==",
                "dn":"7eNuF20uEhbdi6IQ3fB3lg=="
            };

            var processPop=new ProcessDialog();
            processPop.show();
            callPost(baseUrl+"v5/user/emailRegister",params,function(state,response){
                if(state==200){
                    var cookieData=window.setGameCookie(response);
                    parseCookie(cookieData,processPop);
                }else if(state==226){
                    errorLabel.setText(response);
                    processPop.hide();
                }else if(state==406){
                    errorLabel.setText(response);
                    processPop.hide();
                }else if(state==500){
                    errorLabel.setText("Server Busy...");
                    processPop.hide();
                }else{
                    errorLabel.setText("Network Error...");
                    processPop.hide();
                }
            });
        });

    }
}

class LoginDialog extends ModelDialog{
    constructor(width,height){
        super();
        var _that=this;
        var dialogWidth=width*4/5;
        this.contentTable.style.width=dialogWidth+"px";
        

        var crossButton=new CrossIcon();
        crossButton.child.addEventListener("click",function(){
            _that.hide();
        });

        this.contentTable.appendChild(crossButton.child);

        var logoImage=document.createElement("img");
        logoImage.src="img/lock.png";
        logoImage.style.width="80%";
        logoImage.style.height="auto";
        logoImage.style.display="block";
        this.contentTable.appendChild(logoImage);

        var titleLabel=new Label("Login",18);
        titleLabel.child.style.fontWeight="bold";
        titleLabel.child.style.marginBottom="10px";
        this.contentTable.appendChild(titleLabel.child);

       
        var emailBox=new TextBox("img/email.png","Enter your email","email");
    
        var passwordBox=new PasswordBox("Enter your password");

        this.contentTable.appendChild(emailBox.inputDiv);
        this.contentTable.appendChild(passwordBox.inputDiv);
        var forgetLabel=new Label("Forget Password?",8);
        forgetLabel.child.style.color="#0083e8";
        forgetLabel.child.style.textAlign="right";
        forgetLabel.child.style.width="80%"
        forgetLabel.child.style.fontWeight="bold";
        forgetLabel.child.marginBottom="20px";
        this.contentTable.appendChild(forgetLabel.child);

        var termLabel=new TermsLabel("link-login");
        termLabel.child.style.color="black";
        termLabel.child.style.width="80%";
        termLabel.errorLabel.style.width="80%";
        this.contentTable.appendChild(termLabel.child);
        this.contentTable.appendChild(termLabel.errorLabel);


        forgetLabel.child.addEventListener("click",function(){
            if(!emailBox.isValid()){
                errorLabel.setText("Enter Valid Email");
                return;
            }

            var params={
                "emailId":emailBox.getText(),
                "appVersion":"8.1.0",
                "deviceId":window.deviceId
            }

            var processPop=new ProcessDialog();
            processPop.show();
            callPost(baseUrl+"v5/user/forgotPassword",params,function(state,response){
                if(state==200){
                    processPop.hide();
                    errorLabel.setText(response);
                }else{
                    processPop.hide();
                    if(state==500){
                        errorLabel.setText("Server Busy");
                    }else{
                        errorLabel.setText(response);
                    }
                }
            });

        });


        var errorLabel=new Label("",8);
        errorLabel.child.style.color="red";
        errorLabel.child.style.width="80%";
        errorLabel.child.style.textAlign="center";
        errorLabel.child.style.marginBottom="15px";
        this.contentTable.appendChild(errorLabel.child);

        var _that=this;


        this.button.innerHTML="Login";

        var loginButton=this.button;

        this.addButton();


        loginButton.addEventListener("click",function(){

            if(!emailBox.isValid()){
                errorLabel.setText("Enter Valid Email.");
                return;
            }

            if(!passwordBox.isValid()){
                errorLabel.setText("Enter Password.");
                return;
            }

            if(!termLabel.isChecked()){
                termLabel.showError();
                return;
            }
        
            var params={
                "emailId":emailBox.getText(),
                "password":passwordBox.getText(),
                "deviceType":"ANDROID",
                "appVersion":"93",
                "deviceId":window.deviceId+"",
                "isRooted":"false",
                "aName":window.appName,
                "pName":window.packageName,
                "kH":window.keyHash,
                "p":"/data/user/0/best.bulbsmash.cash/files",
                "cp":"/data/data/best.bulbsmash.cash/files",
                "rbs":"false"
            };

            var processPop=new ProcessDialog();
            processPop.show();
            callPost(baseUrl+"v5/user/emailLogin",params,function(state,response){
                if(state==200){
                    var cookieData=window.setGameCookie(response);
                    parseCookie(cookieData,processPop);
                }else if(state==226){
                    errorLabel.setText(response);
                    processPop.hide();
                }else if(state==406){
                    errorLabel.setText(response);
                    processPop.hide();
                }else if(state==500){
                    errorLabel.setText("Server Busy...");
                    processPop.hide();
                }else{
                    errorLabel.setText("Network Error...");
                    processPop.hide();
                }
            });
        });
    }
}

class LoginScreen{
    constructor(width,height){
        var screenHeight=height;
        var screenWidth=width;
        if(width>height){
            screenWidth=screenHeight*9/16;
        }
        var screen=new ModelTable();
        screen.setBackground("url('img/background.png')");
        screen.setSize(screenWidth+"px",screenHeight+"px");

        var logoImage=document.createElement("img");
        logoImage.src="img/logo_small.png";
        screen.add(logoImage).width("70%").horizontalAlign("center").verticalAlign("top");
        screen.row();

        var menuButton=document.createElement("img");
        menuButton.src="img/btn_menu.png";
        screen.add(menuButton).width("40%").horizontalAlign("center");
        screen.row();

        var buttonTable=new ModelTable();
        var facebookButton=document.createElement("img");
        facebookButton.src="img/btn_facebook.png";
        buttonTable.add(facebookButton).width("100%").horizontalAlign("center");
        buttonTable.row();
        var googleButton=document.createElement("img");
        googleButton.src="img/btn_google.png";
        buttonTable.add(googleButton).width("100%").horizontalAlign("center");
        buttonTable.row();
        var termLabel=new TermsLabel("link-splash");
        buttonTable.add(termLabel.child).colspan(2);
        screen.add(buttonTable.table).width("80%").horizontalAlign("center");
        screen.row();
        screen.add(termLabel.errorLabel).width("80%").horizontalAlign("center");
        screen.row();
        this.buttonTable=buttonTable;

        var loginTable=new ModelTable()
        loginTable.table.style.backgroundColor="#00000088";
        loginTable.table.style.borderRadius="7px";
        loginTable.table.style.paddingLeft="10px";
        loginTable.table.style.paddingRight="10px";
        var label1=new Label("Already a member?",14);
        label1.child.style.color="#ffffff";
        loginTable.add(label1.child);
        var label2=new Label("Sign In",14)
        label2.child.style.color="#00f0ff";
        label2.child.style.fontWeight="bold";
        loginTable.add(label2.child);
        label2.child.addEventListener("click",function(){
            var loginPop=new LoginDialog(screenWidth,screenHeight);
            loginPop.show();
        });
        
        screen.add(loginTable.table).horizontalAlign("center");
        screen.row();



        var companyName=document.createElement("div");
        companyName.style.display="block";
        companyName.style.fontSize="8px";
        companyName.style.color="#838383";
        companyName.style.textAlign="center";
        companyName.innerHTML="WITZEAL TECHNOLOGIES PRIVATE LIMITED";
        screen.add(companyName).horizontalAlign("center").verticalAlign("bottom");
        this.companyName=companyName;

        this.screenTable=screen;

        this.screen=screen.table;

        menuButton.addEventListener("click",function(){
            // var qrPop=new QRDialog(function(message){
            //     console.log(message);
            // });
            // qrPop.show();
            var menuPop=new MenuDialog(screenWidth,screenHeight);
            menuPop.show();
        });

        facebookButton.addEventListener("click",function(){
            if(!termLabel.isChecked()){
                termLabel.showError();
                return;
            }
            var processPop=new ProcessDialog();
            processPop.show();
            window.FB.login(function(response){
                if(response.authResponse){
                    callFBLogin(response.authResponse.accessToken,processPop);
                }else{
                    processPop.hide();
                }
            });
        });

        googleButton.addEventListener("click",function(){
            if(!termLabel.isChecked()){
                termLabel.showError();
                return;
            }
            var processPop=new ProcessDialog();
            processPop.show();
            var provider=new firebase.auth.GoogleAuthProvider();
            window.firebase.auth()
            .signInWithPopup(provider)
            .then(function(result){
                var user = result.user;
                user.getIdToken(true)
                .then(function(token){
                    callGoogleLogin("Google",token,processPop);
                })
                .catch(function(error){
                    processPop.hide();
                });
                
            })
            .catch(function(error){
                console.log(error);
                processPop.hide();
            });
            // var registerPop=new RegisterDialog(screenWidth,screenHeight);
            // registerPop.show();
        });

        $(window).resize(function(){
            console.log(window.innerWidth);
            console.log(window.innerHeight);
        });
    }
    show(){
        var cookieString=window.getGameCookie();
        if(cookieString!=null && cookieString!=""){
            var cookieData=JSON.parse(cookieString);
            var processPop=new ProcessDialog();
            processPop.show();
            parseCookie(cookieData,processPop);
        }else{
            window.cookieData=null;
            window.appLaunchResponse=null;
        }
    }
}

window.setLoginScreen();
var loginScreen=new LoginScreen(width,height);
mainDocument.appendChild(loginScreen.screen);
loginScreen.show();


