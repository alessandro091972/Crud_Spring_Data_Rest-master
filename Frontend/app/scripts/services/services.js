'use strict';

var services = angular.module('ngdemoApp.services', ['ngResource']);

var baseUrl = 'http://localhost:8080/springmvcdatarest';  



services.factory('UsersFactory', function ($resource) {
    return $resource(baseUrl + '/api/users', {}, {
        query: { method: 'GET', isArray: true },
        create: { method: 'POST' }
    })
});

services.factory('UserFactory', function ($resource) {
    return $resource(baseUrl + '/api/users/:id', {}, {
        show: { method: 'GET' },
        update: { method: 'PUT', params: {id: '@id'} },
        delete: { method: 'DELETE', params: {id: '@id'} }
    })
});
