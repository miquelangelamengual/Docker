<?php
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\ResizeController;
/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/
Route::get('/file-resize', [ResizeController::class, 'index']);
Route::post('/resize-file', [ResizeController::class, 'resizeImage'])->name('resizeImage');