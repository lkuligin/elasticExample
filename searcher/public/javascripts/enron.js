angular.module("app", []).controller("SearchController", function($scope, $http) {
    $scope.results = [];
    $scope.hits = "";
    $scope.input = " ";
    //§scope.hits = 0;
    $scope.doSearch = function() {
        var httpRequest = $http({
            method : 'GET',
            url : "/search/enron/" + $scope.input,
        }).success(function(data, status) {
            $scope.results = data.results;
            console.log(data.hits)
            $scope.hits = data.hits;
        }).error(function(arg) {
            //alert("error ");
        });

    };
    // run the search when the page loads.
    $scope.doSearch();
});