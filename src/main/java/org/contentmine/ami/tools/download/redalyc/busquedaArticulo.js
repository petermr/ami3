var articulo = angular.module('artjs',[]);

articulo.controller('articleController', ['$scope','$http','$filter','$location',function($scope,$http,$filter,$location){
	$scope.data=[];
	$scope.pageNumbers = [];
	$scope.paginator = 5;
	$scope.currentPage = 1;
    $scope.pageSize = 10;
    $scope.fromPage=($scope.pageSize-$scope.pageSize)+1;
    $scope.toPage=$scope.pageSize;
    $scope.totalArticulos=0;
    $scope.q = '';
    $scope.filterYear=[];
    $scope.filterIdioma=[];
    $scope.filterDisciplina=[];
    $scope.filterPais=[];
    $scope.contPages=1;
	$scope.busqueda = {
		tipo: "articulo",
		textoABuscar: " "
	};
	$scope.cadenaYear="";
	$scope.cadenaIdioma="";
	$scope.cadenaDisciplina="";
	$scope.cadenaPais="";
	$scope.totalPaginas = 0;
	$scope.orderBy = "default";
	$scope.reverseSort = true;
		
	//$scope.textoABuscar = getParametroURL("q") == '' ? '[aA TO Zz]' : getParametroURL("q");
	$scope.textoABuscar = $scope.busqueda.textoABuscar = getParametroURL("q");
	
	$scope.go = function(path){
		//var busqueda = (path.trim() == '' || path == undefined) ? '[aA TO Zz]' : path;
		//window.parent.location.search ='?q='+ busqueda;
		if (path.trim() != '' && path != undefined){
			window.location.search = encodeURI("?q=" + path);
		}
	};
	
	$scope.goEnter = function(keyEvent, texto){ if (keyEvent.wich === 13){ $scope.go(texto); } }
	
	$scope.reverseSort = false;

	if ($scope.textoABuscar.trim() != "" && $scope.textoABuscar.trim() != "[aA TO Zz]"){
		// $scope.orderBy = 'FechaAltaSistema';
		$http({
			url:encodeURI("service/r2020/getArticles/" + $scope.textoABuscar + "/" + $scope.currentPage  + "/" + $scope.pageSize + "/1/"+$scope.orderBy),
			method:"GET"
		}).then(function(response){
			console.log(response.data);
			$scope.noData = response.data.resultados.length > 0 ? false : true;
			$scope.filterData=response.data.filtros;			
			$scope.totalDocumentos = response.data.totalResultados;
			$scope.totalPaginas = Math.ceil($scope.totalDocumentos/$scope.pageSize);
			$scope.initializePageNumbers();
			$scope.totalArticulos=$scope.totalDocumentos;
			if($scope.toPage>$scope.totalArticulos){
				  $scope.toPage=$scope.totalArticulos;
			  }
			$scope.data=response.data.resultados;
			
			if($scope.data.length == 0) 
				document.querySelector("#ar-informe-no-resultados").style.display = "block";
			
			console.log('uno');
			changeTags();
			document.querySelector("#overlay").style.display = "none";
			document.querySelector("#loading").style.display = "none";
		},function(response){
			console.log("Error");
			document.querySelector("#overlay").style.display = "none";
			document.querySelector("#loading").style.display = "none";
		});
	}
	
	$scope.recap=function(e, data){
		console.log("uno");
		
		var resumenes = data.resumen.split('>>>');
		var palabras = data.palabras.split('>>>');
		var idioma = data.language;
		
		for (var i = 0; i < resumenes.length; i++){
			var codigoIdioma = resumenes[i].substring(0, 2);
			if (codigoIdioma.trim() == idioma){
				$scope.resumenArticulo = resumenes[i].substring(3, resumenes[i].length);
				$scope.palabraCortada = palabras[i];
				break;
			}
		}
		
		$scope.recapLang = (idioma == 'es') ? 'RESUMEN' : (idioma == 'pt') ? 'RESUMO' : 'ABSTRACT';
		
		var elemento = jQuery(e.target);
		elemento.parent().find('.modal').attr("style","display: flex;");
	  };
	  
	  $scope.quitarModal = function(e){
		  var elemento = jQuery(e.target);
		  if (elemento.parents('.modal').length == 0){
			  elemento.attr("style","display: none;");
		  }
		  else{
			  elemento.parents('.modal').attr("style","display: none;");
		  }
	  }
	
	function getParametroURL(parametro){
		var parametros = window.parent.location.search.substring(1).split('&');
		
		for (var i = 0; i < parametros.length; i++){
			if (parametros[i].indexOf(parametro + '=') != -1){
				return decodeURI(parametros[i].split('=')[1]);
			}
		}
		return "";
	}
	
	
	var etiquetasResumen = ['', 'es', 'en', 'fr', 'de', 'pt', 'ja', 'it', 'ru', 'ch'];
	$scope.muestraResumen = function(e, resumen, cveIdioma){
		//var idioma = ($scope.lang == "") ? "es" : "en";
		
		var elemento = jQuery(e.target);
		
		console.log(elemento);
		
		if (elemento[0].checked){
			var idioma = etiquetasResumen[cveIdioma];
			var resumenes = resumen.split(">>>");
			var abreviaturaIdioma = "";
			var res = "";
			
			for (var i = 0; i < resumenes.length; i++){
				abreviaturaIdioma = resumenes[i].substring(0, 2);
				if (abreviaturaIdioma.trim() == idioma){
					res = resumenes[i].substring(3, resumenes[i].length);
					break;
				}
			}
			
			elemento.parents('.wrapper').find('.summary').text(res);
			elemento.parents('.wrapper').find('.summary').slideDown();
		}
		else{
			elemento.parents('.wrapper').find('.summary').slideUp();
		}
	}
	
	//Cambio de lista a tabla
	$scope.tipoVista = "lista";
	$scope.cambiaVista = function(vista){
		$scope.tipoVista = vista;
		(function($){
			$.noConflict();
			$("div.searchBox span.vistas").removeClass("border-red");
			$("div.searchBox span."+vista).addClass("border-red");
		})(jQuery);
	}
	
	//Paginación
    $scope.getData = function () {
	      return $filter('filter')($scope.data, $scope.q);
	}
    
    $scope.numberOfPages=function(){
        return Math.ceil($scope.getData().length/$scope.pageSize);                
    }
    
    function changeTags(){
    	jQuery(document).ready(function(){
    		jQuery("div.content div.contentcard div.article-contenido").each(function(){
    			var todo=$(this).text().split("<B>");
    			var txt1=todo[0];
    			var txt2=todo[1].split("<B>")[0];
    			var txt3=todo[1].split("</B>")[1];
    			var html="<p>"+txt1+"<b>"+txt2+"</b>"+txt3+"</p>";
    			jQuery(this).html(html);
    			//console.log("quitando etiquetas");
    		})
    	});
    }
    
  //SORTING
//    $scope.sort = function(keyname){
//		$scope.sortKey = keyname;   
//		$scope.reverse = !$scope.reverse; 
//	}
    
    $scope.sorting = function(orderByField, reverseSort){
    	$scope.reverseSort = !reverseSort;
    	$scope.ordenarPorNum(orderByField, reverseSort);
    };

    //FILTERS
    $scope.campoFiltrado=function(data){
    	if(data.tipo=="País"){
    		if ($scope.filterPais.filter(function(e) { return e.clave === data.clave; }).length > 0) {
    			return true;
    		}
    		else{
    			return false;
    		}
    	}
    	if(data.tipo=="Idioma"){
    		if ($scope.filterIdioma.filter(function(e) { return e.clave === data.clave; }).length > 0) {
    			return true;
    		}
    		else{
    			return false;
    		}
    	}
    	if(data.tipo=="Año"){
    		if ($scope.filterYear.filter(function(e) { return e.clave === data.clave; }).length > 0) {
    			return true;
    		}
    		else{
    			return false;
    		}
    	}
    	if(data.tipo=="Disciplina"){
    		if ($scope.filterDisciplina.filter(function(e) { return e.clave === data.clave; }).length > 0) {
    			return true;
    		}
    		else{
    			return false;
    		}
    	}
    }
    
	$scope.findWithFilters = function(data){
		
		console.log("tache huarache");
		console.log(data);
		
		var opc = -1;
		
		if(data.tipo == "Año") {
			opc=0;
		}else if(data.tipo == "País") {
			opc=3;
		}
		
		for(var i=0; i < $scope.filterData.length; i++){
			if(data.tipo == $scope.filterData[i]['nombre']){
				opc = i;
				break;
			}
		}
		
		switch(opc){
			case 0://Filtro Año
				var index = $scope.filterYear.map(function(filter) {
					  return filter.clave;
				}).indexOf(data.clave);
				
				if(index != -1){//Si la incluye se quita
					$scope.filterYear.splice(index,1);
					
				}else{//Si no la incluye se agrega
					$scope.filterYear.push({"nombre":data.nombre,"clave":data.clave});
				}
				break;
			case 1://Filtro Idioma
				var index = $scope.filterIdioma.map(function(filter) {
					  return filter.clave;
				}).indexOf(data.clave);
				
				if(index != -1){//Si la incluye se quita
					$scope.filterIdioma.splice(index,1);
				}else{//Si no la incluye se agrega
					$scope.filterIdioma.push({"nombre":data.nombre,"clave":data.clave});
				}
				break;
			case 2://Filtro Disciplina
				var index = $scope.filterDisciplina.map(function(filter) {
					  return filter.clave;
				}).indexOf(data.clave);
				
				if(index != -1){//Si la incluye se quita
					$scope.filterDisciplina.splice(index,1);
				}else{//Si no la incluye se agrega
					$scope.filterDisciplina.push({"nombre":data.nombre,"clave":data.clave});
				}
				break;
			case 3://Filtro Pais
				var index = $scope.filterPais.map(function(filter) {
					  return filter.clave;
				}).indexOf(data.clave);
				
				if(index != -1){//Si la incluye se quita
					$scope.filterPais.splice(index,1);
				}else{//Si no la incluye se agrega
					$scope.filterPais.push({"nombre":data.nombre,"clave":data.clave});
				}
				break;
		}		
	}

    $scope.applyFilters=function(){
    	document.querySelector("#overlay").style.display = "block";
		document.querySelector("#loading").style.display = "block";
    	$scope.cadenaPais = "";
    	$scope.cadenaIdioma = "";
    	$scope.cadenaYear = "";
    	$scope.cadenaDisciplina = "";
    	    	
    	if($scope.filterPais.length>0){
    		for(var i=0;i<$scope.filterPais.length;i++){
    			if($scope.cadenaPais==""){
    				$scope.cadenaPais=$scope.filterPais[i].clave;
    			}else{
    				$scope.cadenaPais+="-"+$scope.filterPais[i].clave;
    			}
    		}
    	}
    	if($scope.filterIdioma.length>0){
    		for(var j=0;j<$scope.filterIdioma.length;j++){
    			if($scope.cadenaIdioma==""){
    				$scope.cadenaIdioma=$scope.filterIdioma[j].clave;
    			}else{
    				$scope.cadenaIdioma+="-"+$scope.filterIdioma[j].clave;
    			}
    		}
    	}
    	if($scope.filterYear.length>0){
    		for(var k=0;k<$scope.filterYear.length;k++){
    			if($scope.cadenaYear==""){
    				$scope.cadenaYear=$scope.filterYear[k].clave;
    			}else{
    				$scope.cadenaYear+="-"+$scope.filterYear[k].clave;
    			}
    		}
    	}
    	if($scope.filterDisciplina.length>0){
    		for(var l=0;l<$scope.filterDisciplina.length;l++){
    			if($scope.cadenaDisciplina==""){
    				$scope.cadenaDisciplina=$scope.filterDisciplina[l].clave;
    			}else{
    				$scope.cadenaDisciplina+="-"+$scope.filterDisciplina[l].clave;
    			}
    		}
    	}
	    $scope.currentPage = 1;
		$http({
			url:"service/r2020/getArticles/" + $scope.textoABuscar +"<<<"+$scope.cadenaYear+"<<<"+$scope.cadenaIdioma+"<<<"+$scope.cadenaDisciplina+"<<<"+$scope.cadenaPais+ "/" + $scope.currentPage + "/" + $scope.pageSize + "/1/"+$scope.orderBy,
			method:"GET"
		}).then(function(response){
			//window.data = response;		
			$scope.filterData=response.data.filtros;
			$scope.totalDocumentos = response.data.totalResultados;
			$scope.totalPaginas = Math.ceil($scope.totalDocumentos/$scope.pageSize);
			$scope.totalArticulos=$scope.totalDocumentos;
			$scope.fromPage = ($scope.pageSize-$scope.pageSize)+1;
			$scope.toPage = $scope.pageSize;
			if($scope.toPage>$scope.totalArticulos){$scope.toPage=$scope.totalArticulos;}
			 
			$scope.data=response.data.resultados;
			$scope.noData = response.data.resultados.length>0?false:true;
			console.log('dos');
			console.log($scope.data);
			changeTags();
			document.querySelector("#overlay").style.display = "none";
			document.querySelector("#loading").style.display = "none";
			
			var w = screen.width;		
			if(w <= 780){
				document.getElementById("sidebar").style.width = "0";
			    document.getElementById("contenido").style.marginLeft = "0";
			    document.getElementById("cerrar").style.display = "none";
			    document.getElementById("abrir").style.display = "inline";
			}
		},function(response){
			console.log("Error");
			document.querySelector("#overlay").style.display = "none";
			document.querySelector("#loading").style.display = "none";
		});	
    }; 
    
    $scope.substring = function(string, inicio, fin){
		return (string.length > fin) ? string.replace('&amp;', '%26').substring(inicio, fin).concat('...') : string.replace('&amp;', '%26').substring(inicio, fin);
	};
    
	//Paginador
    $scope.initializePageNumbers = function(){
    	if($scope.totalPaginas >= $scope.paginator){
    		for(var i = 1; i <= $scope.paginator; i++){
    			$scope.pageNumbers.push(i);
    		}
    	}else{
    		for(var i = 1; i <= $scope.totalPaginas; i++){
    			$scope.pageNumbers.push(i);
    		}
    	}
    };
    
    $scope.primerPagina = function(){
    	$scope.currentPage = 1;
    	
    	for(var i = 0; i < $scope.pageNumbers.length; i++){
    		$scope.pageNumbers[i] = (i+1);
    	}
    	
    	$scope.cambiarPaginaB($scope.currentPage);
    };
    
    $scope.ultimaPagina = function (){
    	$scope.currentPage = $scope.totalPaginas;
    	if($scope.totalPaginas < $scope.paginator){
    		for(var i=0; i< $scope.pageNumbers.length; i++){
    			$scope.pageNumbers[i]=(i+1);
    		}
    	}else{
	    	for(var i = 1; i <= $scope.pageNumbers.length; i++){
	    		$scope.pageNumbers[$scope.pageNumbers.length - i] = $scope.totalPaginas + (-i+1);
	    	}
    	}
    	$scope.cambiarPaginaB($scope.currentPage);
    };
    
    $scope.paginaAnteriorSiguiente = function(option){
    	if(option == 'anterior'){
    		$scope.cambiarPaginaB(($scope.currentPage - 1));
    	}else{
    		$scope.cambiarPaginaB(($scope.currentPage + 1));
    	}
    };
    
    $scope.cambiarPaginaB = function (number){
    	var textoServicio = "";
    	//Colocamos el overlay
    	document.getElementById('overlay').style.display = "block";
		document.getElementById('loading').style.display = "block";
    	
    	//Seteamos el valor de las variables
    	$scope.currentPage = number;
    	$scope.fromPage = 1 + ((number-1) * $scope.pageSize);
    	$scope.toPage = number * $scope.pageSize;
    	if($scope.toPage > $scope.totalArticulos) $scope.toPage = $scope.totalArticulos;
    	
    	//Verificamos el uso de filtros para la busqueda
    	if ($scope.filterPais.length>0 || $scope.filterIdioma.length>0 || $scope.filterYear.length>0 || $scope.filterDisciplina.length>0){
    		textoServicio = $scope.textoABuscar;
        	textoServicio = textoServicio + '<<<' + $scope.cadenaYear + '<<<' + $scope.cadenaIdioma + '<<<' + $scope.cadenaDisciplina + '<<<' + $scope.cadenaPais;
    	}
    	else{
    		textoServicio = $scope.textoABuscar;
    	}
    	
    	//Realizamos la consulta para los nuevos resultados
    	$http({
    		url:"service/r2020/getArticles/" + textoServicio + "/" + $scope.currentPage  + "/" + $scope.pageSize + "/0/"+$scope.orderBy,
    		method:"GET"
    	}).then(function(response){
    		$scope.data=response.data.resultados;
    		$scope.noData = response.data.resultados.length>0?false:true;
    		changeTags();
    		//Quitamos el overlay
        	document.getElementById('overlay').style.display = "none";
    		document.getElementById('loading').style.display = "none";
    	},function(response){
    		console.log("Error");
    		//Quitamos el overlay
        	document.getElementById('overlay').style.display = "none";
    		document.getElementById('loading').style.display = "none";
    	});
    	
    	//Acomodamos la paginador
    	if($scope.pageNumbers[$scope.pageNumbers.length - 1] == number){
    		if((number + 3) <= $scope.totalPaginas){ //Queda espacio para recorrer el paginador
    			for(var i = 0; i < $scope.pageNumbers.length; i++){
    				$scope.pageNumbers[i] = number + (i-1);
    			}
    		}else{//No quedan paginas suficientes para recorrer el paginador
    			j = 0;
    			for(var i=($scope.pageNumbers.length-1); i >= 0; i--){
    				$scope.pageNumbers[i] = $scope.totalPaginas - j;
    				j++;
    			}
    		}
    	}else if($scope.pageNumbers[0] == number){
    		if((number - 3) >= 1){
    			for(var i=0; i<$scope.pageNumbers.length; i++){
    				$scope.pageNumbers[i] = (number-3)+i;
    			}
    		}else{
    			for(var i=0; i<$scope.pageNumbers.length; i++){
    				$scope.pageNumbers[i] = (i+1);
    			}
    		}
    	}
    };
    //Fin del Paginador
    
    $scope.volverArriba = function(){
    	jQuery('html, body').animate({
			scrollTop: jQuery('main').offset().top
		}, 500, 'swing');
    };
    
    $scope.ordenarPorNum = function(orderBy, reverseSort){
    	document.querySelector("#overlay").style.display = "block";
		document.querySelector("#loading").style.display = "block";
		$scope.orderBy = reverseSort ? 'FechaAltaSistema' : 'AnioArticulo';
		var textoServicio = "";
    	$scope.currentPage = 1;
    	
    	if ($scope.filterPais.length>0 || $scope.filterIdioma.length>0 || $scope.filterYear.length>0 || $scope.filterDisciplina.length>0){
    		textoServicio = $scope.textoABuscar;
    		console.log($scope.cadenaYear);
        	console.log($scope.cadenaIdioma);
        	console.log($scope.cadenaDisciplina);
        	console.log($scope.cadenaPais);
        	textoServicio = textoServicio + '<<<' + $scope.cadenaYear + '<<<' + $scope.cadenaIdioma + '<<<' + $scope.cadenaDisciplina + '<<<' + $scope.cadenaPais;
    	}
    	else{
    		textoServicio = $scope.textoABuscar;
    	}
    	
    	console.log("service/r2020/getArticles/"+ textoServicio +"/"+ $scope.currentPage +"/"+ $scope.pageSize +"/0/"+ $scope.orderBy);
    	
    	$http({
    		url:"service/r2020/getArticles/"+ textoServicio +"/"+ $scope.currentPage +"/"+ $scope.pageSize +"/0/"+ $scope.orderBy,
    		method:"GET"
    	}).then(function(response){
    		//window.data = response;
    		$scope.data=response.data.resultados;
    		$scope.noData = response.data.resultados.length>0?false:true;
    		console.log($scope.data);
    		console.log("cuatro");
    		changeTags();
    		document.querySelector("#overlay").style.display = "none";
			document.querySelector("#loading").style.display = "none";
    	},function(response){
    		console.log("Error");
    		document.querySelector("#overlay").style.display = "none";
			document.querySelector("#loading").style.display = "none";
    	});
    };
    
    //Control de idioma
	console.log(localStorage["lang"]);
	if(localStorage["lang"] == undefined){
		console.log("entro al if");
		localStorage.setItem("lang","es");
	}
	
	$scope.traducir = function(lang){
		localStorage.setItem("lang", lang);
		$http.get("/redalyc/jsons/traducciones/buscador_articulos-"+lang+".json").then(function (data){
			$scope.t = data.data;
		}).catch(function(status){
			alert("Error - No se puede abrir el archivo");
		});
	}
	
	$scope.traducir(localStorage.getItem("lang"));
	
	$scope.clickListener = function(e,flag){
		
		e.target.style.display = "none";
		
		var nameSplited = e.target.id.split("-");
		if (nameSplited[0] == "menos"){
			document.getElementById("mas-"+nameSplited[1]).style.display = "block";
		}else{
			document.getElementById("menos-"+nameSplited[1]).style.display = "block";
		}
		
		if(flag){
			for(var i=0; i<e.target.parentNode.childNodes.length; i++){
				if(e.target.parentNode.childNodes[i].className == 'elemento-filtro-hidden'){
					e.target.parentNode.childNodes[i].style.display = "block";
				}
			}
		}else{
			for(var i=0; i<e.target.parentNode.childNodes.length; i++){
				if(e.target.parentNode.childNodes[i].className == 'elemento-filtro-hidden'){
					e.target.parentNode.childNodes[i].style.display = "none";
				}
			}
		}
		
	};

}]);

articulo.filter('startFrom', function() {
    return function(input, start) {
        start = +start; 
        return input.slice(start);
    };
});

articulo.filter('html', function($sce){
	return function(val){
		return $sce.trustAsHtml(val);
	};
});

//navbar
function mostrar() {
	(function($){
		$.noConflict();		
		if(screen.width <= 600){
			console.log("sidebar");
			document.getElementById("sidebar").style.width = "100%";
		    document.getElementById("contenido").style.marginLeft = "0";
		    document.getElementById("abrir").style.display = "none";
		    document.getElementById("cerrar").style.display = "inline";
		    $("#cerrar").attr("style","position: absolute; z-index:6; color: white; right: 5%");
		}else if(screen.width > 600 && screen.width <= 780){
			console.log("sidebar");
			document.getElementById("sidebar").style.width = "45%";
		    document.getElementById("contenido").style.marginLeft = "0";
		    document.getElementById("abrir").style.display = "none";
		    document.getElementById("cerrar").style.display = "inline";
		    $("#cerrar").attr("style","position: absolute; z-index:6; color: white; left: 41%");
		}else{
		    document.getElementById("sidebar").style.width = "19%";
		    document.getElementById("contenido").style.marginLeft = "19%";
		    document.getElementById("abrir").style.display = "none";
		    document.getElementById("cerrar").style.display = "inline";
		}
	})(jQuery); 
}

function ocultar() {
	(function($){
		$.noConflict();
		if(screen.width < 600){
			document.getElementById("sidebar").style.width = "0";
		    document.getElementById("contenido").style.marginLeft = "0";
		    document.getElementById("cerrar").style.display = "none";
		    document.getElementById("abrir").style.display = "inline";
		}else{
		    document.getElementById("sidebar").style.width = "0";
		    document.getElementById("contenido").style.marginLeft = "0";
		    document.getElementById("cerrar").style.display = "none";
		    document.getElementById("abrir").style.display = "inline";
		}
	})(jQuery); 
}


function cuentaDocumentos(elementos){
	var total = 0;
	
	for (var i = 0; i < elementos.length; i++){
		total = total + elementos[i].total;
	}
	
	return total;
}


(function($){
	$.noConflict();
	
	$(document).ready(function(){
		if(screen.width <= 768) {
    		$("div#sidebar").attr("style","width: 0;");
    		document.getElementById("cerrar").style.display = "none";
    		document.getElementById("abrir").style.display = "inline";
    	}
		
		$("span.vistas").click(function(){
			$("span.vistas").removeClass("border-red");
			$(this).addClass("border-red");
		});
		
		function getParametroURL(parametro){
			var parametros = window.parent.location.search.substring(1).split('&');
		
			for (var i = 0; i < parametros.length; i++){
				if (parametros[i].indexOf(parametro + '=') != -1){
					return decodeURI(parametros[i].split('=')[1]);
				}
			}
			return "";
		}
		
		var lang = getParametroURL("lang");	
		if(lang){
			$.each($(".tr"), function( i, url ){
				var href = $(this).attr("href");
				
				if(href.indexOf("lang") > 0){
					console.log("sustituir");
					href = href.replace(/es|en/gi, function (x) {
						return lang;
					});
					$(this).attr("href", href);
				}else{
					$(this).attr("href", url + "&lang="+ lang);
				}
				
				href = $(this).attr("href");
				if(href.indexOf("?") < 0){
					href = href.replace(/&/gi, function (x) {
						return "?";
					});
					$(this).attr("href", href);
				}
	        });
		}
		
	});
	
})(jQuery); 