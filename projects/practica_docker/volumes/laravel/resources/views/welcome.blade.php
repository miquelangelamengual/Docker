<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css">
    <title>Laravel Image Resize Example</title>
</head>
<body>
    <div class="container mt-5" style="max-width: 550px">
        <h2 class="mb-5">Laravel Image Resize Example</h2>     
        <form action="{{route('resizeImage')}}" method="post" enctype="multipart/form-data">
            @csrf
            @if ($message = Session::get('success'))
            <div class="alert alert-success">
                <strong>{{ $message }}</strong>
            </div>

            <div class="col-md-12 mb-3">
                <strong>Grayscale Image:</strong><br/>
                <img src="/uploads/{{ Session::get('fileName') }}" width="550px" />
            </div>
            @endif
            @if (count($errors) > 0)
            <div class="alert alert-danger">
                <ul>
                    @foreach ($errors->all() as $error)
                    <li>{{ $error }}</li>
                    @endforeach
                </ul>
            </div>
            @endif
            <div class="custom-file">
                <input type="file" name="file" class="custom-file-input" id="chooseFile">
                <label class="custom-file-label" for="chooseFile">Select file</label>
            </div>
            <button type="submit" name="submit" class="btn btn-outline-danger btn-block mt-4">
                Upload
            </button>
        </form>
    </div>
</body>
</html>