function clearFeed($scope) {
    $scope.feeduser = null;
    $scope.feed = null;
    $scope.user = null;
    $scope.authenticated = false;
}
angular.module('sso', [ 'ngRoute', 'ngResource' ]).config(
		function($routeProvider, $locationProvider) {
            $locationProvider.html5Mode(true);

			$routeProvider.otherwise('/');
            $routeProvider.when('/@:username', {
                templateUrl : 'feed.html',
                controller : 'feed'
			}).when('/profile', {
				templateUrl : 'profile.html',
				controller : 'profile'
            }).when('/', {
                templateUrl : 'feed.html',
                controller : 'feed'
			});

}).service('userService', function($http) {
        this.user = $http.get('/profile/user');
        this.getUser = function() {
            return this.user;
        };
}).controller('navigation', function($scope, $http, $window, $route, userService) {
	$scope.tab = function(route) {
		return $route.current && route === $route.current.controller;
	};
	if (!$scope.user) {
        userService.getUser().then(function(user) {
            console.log("nav user: %o", user);
            if (user.data.principal) {
                $scope.user = user.data.principal;
                $scope.authenticated = true;
            }
		}, function() {
            $scope.user = null;
			$scope.authenticated = false;
		});
	}
	$scope.logout = function() {
		$http.post('/profile/logout', {}).success(function() {
            clearFeed($scope);
			// Force reload of home page to reset all state after logout
			$window.location.href = '/';
		});
	};
}).controller('feed', function($scope, $resource, $route, userService) {
        console.log("feed route.current.params.username: %o", $route.current.params.username);
        userService.getUser().then(function(user) {
            console.log("feed user: %o", user);
            var feeduser = user.data.principal;

            if ($route.current.params.username) {
                feeduser = $route.current.params.username;
            }

            if (feeduser) {
                $scope.feeduser = feeduser;

                $resource("/feed/@" + feeduser, {}).get({}, function (feed) {
                    $scope.feed = feed;
                }, function (err) {
                    console.log("Error getting feed: " + err);
                });

                $scope.user = user;
                $scope.authenticated = true;
            } else {
                clearFeed($scope);
            }
        }, function(err) {
            console.log("Error getting user: "+err);
            clearFeed($scope);
        });
}).controller('profile', function($scope, $resource) {

	$resource('/profile/message', {}).get({}, function(data) {
		$scope.message = data.message;
	}, function(err) {
        console.log("Error getting message: "+err);
		$scope.message = '';
	});

});