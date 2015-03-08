(function() {
    var app = angular.module('website',  ['angularTreeview'] );
    app.config(['$httpProvider', function ($httpProvider) {
        //Reset headers to avoid OPTIONS request (aka preflight)
        $httpProvider.defaults.headers.common = {};
        $httpProvider.defaults.headers.post = {};
        $httpProvider.defaults.headers.put = {};
        $httpProvider.defaults.headers.patch = {};
    }]);

    var wc = app.controller("WebsiteController", ['$http', function($http){ 
        var ctrl = this;

        ctrl.websites = [];

        $http.get('http://localhost:9000/websites').success(function(data){
            console.log(data);
            ctrl.websites = data;
        });

        ctrl.addWebsite = function(){
            console.log(ctrl.website);

            ctrl.website.id = 1;
            ctrl.website.path = "";
            ctrl.website.proxy = false;

            $http({
                url: 'http://localhost:9000/websites',
                dataType: 'json',
                method: 'POST',
                data: ctrl.website,
                headers: {
                    "Content-Type": "application/json"
                }
            }).success(function(data){
                ctrl.websites.push(data);
                ctrl.website = {};
            });
        };
    }]);
  
    var sc = app.controller("SourceController", ['$http', function($http){ 
        var ctrl = this;

        ctrl.sources = [];

        $http.get('http://localhost:9000/sources').success(function(data){
            console.log(data);
            ctrl.sources = data;
        });

    }]);

    var rc = app.controller("RuleController", ['$http', function($http){ 
        var ctrl = this;

        ctrl.rules = [];

        $http.get('http://localhost:9000/rules').success(function(data){
            console.log(data);
            ctrl.rules = data;
        });

    }]);

    var rc2 = app.controller("AggregationRuleController", ['$http', function($http){ 
        var ctrl = this;

        ctrl.rules = [];

        $http.get('http://localhost:9000/aggregationrules').success(function(data){
            console.log(data);
            ctrl.rules = data;
        });

    }]);
    
     //test controller
    var mc = app.controller('myController', function($scope){
    
      	//test tree model 1
        $scope.websites = [
            { "roleName" : "Websites", "roleId" : "websites", "children" : [
              { "roleName" : "Add new website", "roleId" : "add-website", "children" : [] },
              { "roleName" : "site1", "roleId" : "website", "children" : [
                { "roleName" : "routing rule", "roleId" : "routing-rule", "children" : [] },
                { "roleName" : "aggregation rule", "roleId" : "aggregation-rule", "children" : [] },
                { "roleName" : "logs", "roleId" : "logs", "children" : [] },
                { "roleName" : "preview", "roleId" : "preview", "target":"http://localhost:10001/", "children" : [] }
              ]},
              { "roleName" : "site2", "roleId" : "website", "children" : [
                { "roleName" : "routing rule", "roleId" : "routing-rule", "children" : [] },
                { "roleName" : "aggregation rule", "roleId" : "aggregation-rule", "children" : [] },
                { "roleName" : "logs", "roleId" : "logs", "children" : [] },
                { "roleName" : "preview", "roleId" : "preview", "target":"http://localhost:10002/", "children" : [] }
              ]}
            ]},
            { "roleName" : "Sources", "roleId" : "sources", "children" : [
                          { "roleName" : "article", "roleId" : "article", "children" : [] }
            ]}
          ];
          
        $scope.currentNode = $scope.websites[0];
        $scope.currentNode.selected = 'selected';
    
      });
      

}());

