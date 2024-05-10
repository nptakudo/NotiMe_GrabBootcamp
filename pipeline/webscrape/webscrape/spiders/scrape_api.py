from flask import Flask, request, jsonify
import subprocess
import json

app = Flask(__name__)

@app.route('/execute_command', methods=['POST'])
def execute_command():
    # Get the command from the request JSON data
    data = request.get_json()
    bash_command = data.get('command')

    if not bash_command:
        return jsonify({'error': 'No command provided'}), 400

    try:
        # Execute the Bash command using subprocess
        subprocess.call(bash_command, shell=True)

        # Read the contents of output.json file
        with open('output.json', 'r') as file:
            output_data = json.load(file)
        
        return jsonify(output_data), 200
    except Exception as e:
        return jsonify({'error': f'Command execution failed: {str(e)}'}), 500

if __name__ == '__main__':
    app.run(debug=True)
