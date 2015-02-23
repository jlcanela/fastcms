(function() {
    var app = angular.module('website', [ ]);
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
}());

