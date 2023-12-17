import os
import librosa
import numpy as np
import pandas as pd
import matplotlib as mpl
import matplotlib.pyplot as plt
import ipywidgets as widgets
import seaborn as sns
from sklearn import preprocessing

# plotting style
sns.set_style("whitegrid")
sns.set_palette("muted")

# format plot options
plt.rcParams["xtick.labelsize"] = 14
plt.rcParams["ytick.labelsize"] = 14
plt.rcParams["patch.force_edgecolor"] = True


import os
import librosa
import librosa.display
import matplotlib.pyplot as plt

# Define the root directory
root_dir = '/content/drive/MyDrive/audios'

# Directory containing the WAV files
audio_dir = root_dir

# List all WAV files in the audio directory
wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

# Display the first few WAV file names
print("WAV Files:")
print(wav_files[:5])

# Now, you can iterate through the list of WAV files and process them as needed.
# Example code to load and process a WAV file:
for wav_file in wav_files:
    current_wav_file = os.path.join(audio_dir, wav_file)
    y, sr = librosa.load(current_wav_file)

    # Display the waveform
    plt.figure(figsize=(12, 4))
    librosa.display.waveshow(y, sr=sr)
    plt.title(f'Waveform - {wav_file}')
    plt.show()

    # Display the spectrogram
    D = librosa.amplitude_to_db(librosa.stft(y), ref=np.max)
    plt.figure(figsize=(12, 4))
    librosa.display.specshow(D, sr=sr, x_axis='time', y_axis='log')
    plt.colorbar(format='%+2.0f dB')
    plt.title(f'Spectrogram - {wav_file}')
    plt.show()

import os
import pandas as pd
import librosa

# Define the root directory
root_dir = '/content/drive/MyDrive/audios'

# Directory containing the WAV files
audio_dir = root_dir

# List all WAV files in the audio directory
wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

# Initialize an empty DataFrame to store audio features
columns = ['filename', 'duration', 'sampling_rate']
tp_df = pd.DataFrame(columns=columns)

# Iterate through the list of WAV files
for wav_file in wav_files:
    current_wav_file = os.path.join(audio_dir, wav_file)

    # Load the audio file and extract features
    y, sr = librosa.load(current_wav_file)
    duration = librosa.get_duration(y=y, sr=sr)

    # Append the features to the DataFrame
    tp_df = tp_df.append({'filename': wav_file, 'duration': duration, 'sampling_rate': sr}, ignore_index=True)

# Display the first few rows of the DataFrame
print(tp_df.head())

tp_df.info()

# Commented out IPython magic to ensure Python compatibility.
# %matplotlib inline
import seaborn as sns

# Define the root directory
root_dir = '/content/drive/MyDrive/audios'

# Directory containing the WAV files
audio_dir = root_dir

# List all WAV files in the audio directory
wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

# Initialize an empty DataFrame to store audio features
columns = ['filename', 'duration', 'sampling_rate']
tp_df = pd.DataFrame(columns=columns)

# Iterate through the list of WAV files
for wav_file in wav_files:
    current_wav_file = os.path.join(audio_dir, wav_file)

    # Load the audio file and extract features
    y, sr = librosa.load(current_wav_file)
    duration = librosa.get_duration(y=y, sr=sr)

    # Append the features to the DataFrame
    tp_df = tp_df.append({'filename': wav_file, 'duration': duration, 'sampling_rate': sr}, ignore_index=True)

# Explore the distribution of audio file durations
plt.figure(figsize=(12, 6))
sns.histplot(tp_df['duration'], bins=30, kde=True, color='navy')
plt.title('Distribution of Audio File Durations')
plt.xlabel('Duration (seconds)')
plt.ylabel('Count')
plt.show()

import numpy as np
import scipy.signal
import matplotlib.pyplot as plt
from scipy.io import wavfile
import os

# Define the root directory
root_dir = '/content/drive/MyDrive/audios'

# Directory containing the WAV files
audio_dir = root_dir

# List all WAV files in the audio directory
wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

# Define the high-pass filter parameters
cutoff_frequency = 500  # in Hertz
filter_order = 3        # increased filter order
new_sample_rate = 41000  # Adjust the sampling rate

# Iterate through the list of WAV files
for wav_file in wav_files:
    current_wav_file = os.path.join(audio_dir, wav_file)

    # Load the audio file
    sample_rate, audio_data = wavfile.read(current_wav_file)

    # Resample the audio data to the new sampling rate
    audio_data_resampled = scipy.signal.resample(audio_data, int(len(audio_data) * new_sample_rate / sample_rate))

    # Design the high-pass filter using butterworth filter
    b, a = scipy.signal.butter(filter_order, cutoff_frequency, btype='high', fs=new_sample_rate, analog=False)

    # Apply the filter to the resampled audio signal
    filtered_audio = scipy.signal.lfilter(b, a, audio_data_resampled)

    # Save the filtered audio to a new WAV file
    filtered_wav_file = os.path.join(audio_dir, f"filtered_{wav_file}")
    wavfile.write(filtered_wav_file, new_sample_rate, filtered_audio)

    # Plot the original and filtered signals (optional)
    time = np.arange(0, len(filtered_audio)) / new_sample_rate
    plt.figure(figsize=(12, 6))
    plt.plot(time, audio_data_resampled, label='Original Audio Signal (Resampled)')
    plt.plot(time, filtered_audio, label='Filtered Audio Signal (High-pass)')
    plt.title(f'Audio Signal with High-pass Filter - {wav_file}')
    plt.xlabel('Time (seconds)')
    plt.ylabel('Amplitude')
    plt.legend()
    plt.show()

import os
import numpy as np
import matplotlib.pyplot as plt
import librosa
import librosa.display

def analyze_audio_file(file_path):
    # Load the audio file
    y, sr = librosa.load(file_path, sr=None)

    # Compute the spectrogram
    D = librosa.amplitude_to_db(np.abs(librosa.stft(y)), ref=np.max)

    # Set parameters for analysis
    threshold_db = 6  # threshold for detecting >6dB rises and falls
    min_freq = 1000
    max_freq = 5000
    min_interval = 1/10  # seconds
    max_interval = 1/3   # seconds
    max_variation = 0.2  # 20% time variation

    # Find events exceeding the threshold
    events = librosa.effects.split(y, top_db=threshold_db)

    valid_intervals = []

    # Analyze events
    for start, end in events:
        # Extract the portion of the spectrogram corresponding to the event
        event_spectrogram = D[:, start:end]

        # Compute the frequency range of interest
        freq_bins = librosa.core.fft_frequencies(sr=sr)

        freq_indices = np.where((freq_bins >= min_freq) & (freq_bins <= max_freq))[0]

        # Compute the sum of energy within the frequency range
        energy_sum = np.sum(event_spectrogram[freq_indices, :])

        # Compute the duration of the event
        duration = librosa.get_duration(y=y[start:end], sr=sr)

        # Compute the interval between the start of consecutive events
        if len(valid_intervals) > 0:
            interval = start - valid_intervals[-1]
            interval_sec = interval / sr

            # Check if the interval is within the specified range and variation
            if min_interval <= interval_sec <= max_interval and interval_sec * max_variation > interval_sec:
                print(f"Valid interval: {interval_sec} seconds")

        # Store the current event's end for future comparisons
        valid_intervals.append(end)

        # Additional analysis based on energy_sum and duration can be added here

if __name__ == "__main__":
    # Define the root directory
    root_dir = '/content/drive/MyDrive/audios'

    # Directory containing the WAV files
    audio_dir = root_dir

    # List all WAV files in the audio directory
    wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

    # Iterate through the list of WAV files
    for wav_file in wav_files:
        current_wav_file = os.path.join(audio_dir, wav_file)
        print(f"Analyzing {wav_file}")
        analyze_audio_file(current_wav_file)

import os
import numpy as np
import librosa
import librosa.display

def analyze_audio_file(file_path):
    # Load the audio file
    y, sr = librosa.load(file_path, sr=None)

    # Set parameters for analysis
    threshold_db = 6  # threshold for detecting >6dB rises and falls
    min_freq_range = 300  # minimum frequency range width in Hz
    min_repetitions = 3  # minimum repetitions per second
    max_repetitions = 10  # maximum repetitions per second
    max_variation = 0.2  # 20% time variation

    # Compute the spectrogram
    D = librosa.amplitude_to_db(np.abs(librosa.stft(y)), ref=np.max)

    # Find events exceeding the threshold
    events = librosa.effects.split(y, top_db=threshold_db)

    # Analyze events
    for i in range(len(events)):
        start, end = events[i]

        # Extract the portion of the spectrogram corresponding to the event
        event_spectrogram = D[:, start:end]

        # Compute the frequency range of interest
        freq_bins = librosa.core.fft_frequencies(sr=sr)

        # Check for frequency range width > 300Hz
        for j in range(len(freq_bins)):
            if j + min_freq_range < len(freq_bins):
                freq_range = freq_bins[j:j + min_freq_range]

                # Compute the sum of energy within the frequency range
                energy_sum = np.sum(event_spectrogram[j:j + min_freq_range, :])

                # Compute the duration of the event
                duration = librosa.get_duration(y=y[start:end], sr=sr)

                # Compute the interval between the start of consecutive events
                if i > 0 and len(events[i - 1]) > 1:
                    interval = start - events[i - 1][1]
                    interval_sec = interval / sr

                    # Check if the interval is within the specified range and variation
                    if min_repetitions <= 1 / interval_sec <= max_repetitions and interval_sec * max_variation > interval_sec:
                        print(f"Valid interval: {interval_sec} seconds")
                        print(f"Frequency range: {freq_range} Hz")
                        print(f"Energy sum: {energy_sum}")
                        print(f"Duration: {duration} seconds")
                        print("")

# Directory containing the WAV files
audio_dir = '/content/drive/MyDrive/audios'

# List all WAV files in the audio directory
wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

# Iterate through the list of WAV files
for wav_file in wav_files:
    current_wav_file = os.path.join(audio_dir, wav_file)
    print(f"Analyzing {wav_file}")
    analyze_audio_file(current_wav_file)

import os
import numpy as np
import librosa
import librosa.display

def analyze_audio_file(file_path):
    # Load the audio file
    y, sr = librosa.load(file_path, sr=None)

    # Set parameters for analysis
    threshold_db = 6  # threshold for detecting >6dB rises and falls
    min_freq_range = 300  # minimum frequency range width in Hz
    min_repetitions = 3  # minimum repetitions per second
    max_repetitions = 10  # maximum repetitions per second
    max_variation = 0.2  # 20% time variation

    # Compute the spectrogram
    D = librosa.amplitude_to_db(np.abs(librosa.stft(y)), ref=np.max)

    # Find events exceeding the threshold
    events = librosa.effects.split(y, top_db=threshold_db)

    # Count occurrences
    occurrences_count = 0

    # Analyze events
    for i in range(len(events)):
        start, end = events[i]

        # Extract the portion of the spectrogram corresponding to the event
        event_spectrogram = D[:, start:end]

        # Compute the frequency range of interest
        freq_bins = librosa.core.fft_frequencies(sr=sr)

        # Check for frequency range width > 300Hz
        for j in range(len(freq_bins)):
            if j + min_freq_range < len(freq_bins):
                freq_range = freq_bins[j:j + min_freq_range]

                # Compute the sum of energy within the frequency range
                energy_sum = np.sum(event_spectrogram[j:j + min_freq_range, :])

                # Compute the duration of the event
                duration = librosa.get_duration(y=y[start:end], sr=sr)

                # Compute the interval between the start of consecutive events
                if i > 0 and len(events[i - 1]) > 1:
                    interval = start - events[i - 1][1]
                    interval_sec = interval / sr

                    # Check if the interval is within the specified range and variation
                    if min_repetitions <= 1 / interval_sec <= max_repetitions and interval_sec * max_variation > interval_sec:
                        print(f"Valid interval: {interval_sec} seconds")
                        print(f"Frequency range: {freq_range} Hz")
                        print(f"Energy sum: {energy_sum}")
                        print(f"Duration: {duration} seconds")
                        print("")

                        # Increment the occurrences count
                        occurrences_count += 1

    return occurrences_count

# Directory containing the WAV files
audio_dir = '/content/drive/MyDrive/audios'

# List all WAV files in the audio directory
wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

# Iterate through the list of WAV files
for wav_file in wav_files:
    current_wav_file = os.path.join(audio_dir, wav_file)
    print(f"Analyzing {wav_file}")
    count = analyze_audio_file(current_wav_file)
    print(f"Total occurrences: {count}")
    print("")



import os
import numpy as np
import librosa
import librosa.display

def analyze_audio_file(file_path):
    # Load the audio file
    y, sr = librosa.load(file_path, sr=None)

    # Set parameters for analysis
    threshold_db = 6  # threshold for detecting >6dB rises and falls
    min_freq_range = 300  # minimum frequency range width in Hz
    min_repetitions = 3  # minimum repetitions per second
    max_repetitions = 10  # maximum repetitions per second
    max_variation = 0.2  # 20% time variation
    max_peak_time_diff = 0.005  # maximum time difference for peak alignment

    # Compute the spectrogram
    D = librosa.amplitude_to_db(np.abs(librosa.stft(y)), ref=np.max)

    # Find events exceeding the threshold
    events = librosa.effects.split(y, top_db=threshold_db)

    # Dictionary to store frequency ranges, their occurrences, and peak timings
    freq_ranges = {}

    # Analyze events
    for i in range(len(events)):
        start, end = events[i]

        # Extract the portion of the spectrogram corresponding to the event
        event_spectrogram = D[:, start:end]

        # Compute the frequency range of interest
        freq_bins = librosa.core.fft_frequencies(sr=sr)

        # Check for frequency range width > 300Hz
        for j in range(len(freq_bins)):
            if j + min_freq_range < len(freq_bins):
                freq_range = freq_bins[j:j + min_freq_range]

                # Compute the sum of energy within the frequency range
                energy_sum = np.sum(event_spectrogram[j:j + min_freq_range, :])

                # Compute the duration of the event
                duration = librosa.get_duration(y=y[start:end], sr=sr)

                # Compute the interval between the start of consecutive events
                if i > 0 and len(events[i - 1]) > 1:
                    interval = start - events[i - 1][1]
                    interval_sec = interval / sr

                    # Check if the interval is within the specified range and variation
                    if min_repetitions <= 1 / interval_sec <= max_repetitions and interval_sec * max_variation > interval_sec:
                        # Add the frequency range, occurrences, and peak timings to the dictionary
                        if tuple(freq_range) not in freq_ranges:
                            freq_ranges[tuple(freq_range)] = {'occurrences': 1, 'peaks': [start]}
                        else:
                            freq_ranges[tuple(freq_range)]['occurrences'] += 1
                            freq_ranges[tuple(freq_range)]['peaks'].append(start)

    # Check for peaks alignment within the specified time difference
    for freq_range, data in freq_ranges.items():
        occurrences = data['occurrences']
        peaks = data['peaks']

        for i in range(occurrences - 1):
            for j in range(i + 1, occurrences):
                time_diff = np.abs(peaks[i] - peaks[j]) / sr

                if time_diff <= max_peak_time_diff:
                    print(f"Peaks in frequency range {freq_range} at {peaks[i]}s and {peaks[j]}s are within {max_peak_time_diff}s")

# Directory containing the WAV files
audio_dir = '/content/drive/MyDrive/audios'

# List all WAV files in the audio directory
wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

# Iterate through the list of WAV files
for wav_file in wav_files:
    current_wav_file = os.path.join(audio_dir, wav_file)
    print(f"Analyzing {wav_file}")
    analyze_audio_file(current_wav_file)
    print("")

import os
import numpy as np
import librosa
import librosa.display

def analyze_audio_file(file_path):
    # Load the audio file
    y, sr = librosa.load(file_path, sr=None)

    # Set parameters for analysis
    threshold_db = 6  # threshold for detecting >6dB rises and falls
    min_freq_range = 300  # minimum frequency range width in Hz
    min_repetitions = 3  # minimum repetitions per second
    max_repetitions = 10  # maximum repetitions per second
    max_variation = 0.2  # 20% time variation
    max_peak_time_diff = 0.005  # maximum time difference for peak alignment

    # Compute the spectrogram
    D = librosa.amplitude_to_db(np.abs(librosa.stft(y)), ref=np.max)

    # Find events exceeding the threshold
    events = librosa.effects.split(y, top_db=threshold_db)

    # Dictionary to store frequency ranges, their occurrences, peak timings, and max amplitude rise
    freq_ranges = {}

    # Analyze events
    for i in range(len(events)):
        start, end = events[i]

        # Extract the portion of the spectrogram corresponding to the event
        event_spectrogram = D[:, start:end]

        # Compute the frequency range of interest
        freq_bins = librosa.core.fft_frequencies(sr=sr)

        # Check for frequency range width > 300Hz
        for j in range(len(freq_bins)):
            if j + min_freq_range < len(freq_bins):
                freq_range = freq_bins[j:j + min_freq_range]

                # Compute the sum of energy within the frequency range
                energy_sum = np.sum(event_spectrogram[j:j + min_freq_range, :])

                # Compute the duration of the event
                duration = librosa.get_duration(y=y[start:end], sr=sr)

                # Compute the interval between the start of consecutive events
                if i > 0 and len(events[i - 1]) > 1:
                    interval = start - events[i - 1][1]
                    interval_sec = interval / sr

                    # Check if the interval is within the specified range and variation
                    if min_repetitions <= 1 / interval_sec <= max_repetitions and interval_sec * max_variation > interval_sec:
                        # Add the frequency range, occurrences, peak timings, and max amplitude rise to the dictionary
                        if tuple(freq_range) not in freq_ranges:
                            freq_ranges[tuple(freq_range)] = {'occurrences': 1, 'peaks': [start], 'max_amplitude_rise': energy_sum}
                        else:
                            freq_ranges[tuple(freq_range)]['occurrences'] += 1
                            freq_ranges[tuple(freq_range)]['peaks'].append(start)
                            freq_ranges[tuple(freq_range)]['max_amplitude_rise'] = max(freq_ranges[tuple(freq_range)]['max_amplitude_rise'], energy_sum)

    # Print the highest Hz at which amplitude rise is >6dB for each frequency range
    for freq_range, data in freq_ranges.items():
        max_amplitude_rise = data['max_amplitude_rise']
        print(f"For frequency range {freq_range}, the highest Hz at which amplitude rise is >6dB is: {max_amplitude_rise} Hz")

# Directory containing the WAV files
audio_dir = '/content/drive/MyDrive/audios'

# List all WAV files in the audio directory
wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

# Iterate through the list of WAV files
for wav_file in wav_files:
    current_wav_file = os.path.join(audio_dir, wav_file)
    print(f"Analyzing {wav_file}")
    analyze_audio_file(current_wav_file)
    print("")

import os
import numpy as np
import librosa
import librosa.display

def analyze_audio_file(file_path):
    # Load the audio file
    y, sr = librosa.load(file_path, sr=None)

    # Set parameters for analysis
    threshold_db = 6  # threshold for detecting >6dB rises and falls
    min_freq_range = 300  # minimum frequency range width in Hz
    min_repetitions = 3  # minimum repetitions per second
    max_repetitions = 10  # maximum repetitions per second
    max_variation = 0.2  # 20% time variation
    max_peak_time_diff = 0.005  # maximum time difference for peak alignment

    # Compute the spectrogram
    D = librosa.amplitude_to_db(np.abs(librosa.stft(y)), ref=np.max)

    # Find events exceeding the threshold
    events = librosa.effects.split(y, top_db=threshold_db)

    # Dictionary to store frequency ranges, their occurrences, peak timings, and max amplitude rise
    freq_ranges = {}

    # Analyze events
    for i in range(len(events)):
        start, end = events[i]

        # Extract the portion of the spectrogram corresponding to the event
        event_spectrogram = D[:, start:end]

        # Compute the frequency range of interest
        freq_bins = librosa.core.fft_frequencies(sr=sr)

        # Check for frequency range width > 300Hz
        for j in range(len(freq_bins)):
            if j + min_freq_range < len(freq_bins):
                freq_range = freq_bins[j:j + min_freq_range]

                # Compute the sum of energy within the frequency range
                energy_sum = np.sum(event_spectrogram[j:j + min_freq_range, :])

                # Compute the duration of the event
                duration = librosa.get_duration(y=y[start:end], sr=sr)

                # Compute the interval between the start of consecutive events
                if i > 0 and len(events[i - 1]) > 1:
                    interval = start - events[i - 1][1]
                    interval_sec = interval / sr

                    # Check if the interval is within the specified range and variation
                    if min_repetitions <= 1 / interval_sec <= max_repetitions and interval_sec * max_variation > interval_sec:
                        # Add the frequency range, occurrences, peak timings, and max amplitude rise to the dictionary
                        if tuple(freq_range) not in freq_ranges:
                            freq_ranges[tuple(freq_range)] = {'occurrences': 1, 'peaks': [start], 'max_amplitude_rise': energy_sum}
                        else:
                            freq_ranges[tuple(freq_range)]['occurrences'] += 1
                            freq_ranges[tuple(freq_range)]['peaks'].append(start)
                            freq_ranges[tuple(freq_range)]['max_amplitude_rise'] = max(freq_ranges[tuple(freq_range)]['max_amplitude_rise'], energy_sum)

    # Print the frequency ranges for each identified band
    for idx, (freq_range, data) in enumerate(freq_ranges.items()):
        min_freq = min(freq_range)
        max_freq = max(freq_range)
        print(f"RANGE {idx + 1} = {min_freq}Hz - {max_freq}Hz")

# Directory containing the WAV files
audio_dir = '/content/drive/MyDrive/audios'

# List all WAV files in the audio directory
wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

# Iterate through the list of WAV files
for wav_file in wav_files:
    current_wav_file = os.path.join(audio_dir, wav_file)
    print(f"Analyzing {wav_file}")
    analyze_audio_file(current_wav_file)
    print("")

import os
import numpy as np
import librosa
import librosa.display

def analyze_audio_file(file_path):
    # Load the audio file
    y, sr = librosa.load(file_path, sr=None)

    # Set parameters for analysis
    threshold_db = 6  # threshold for detecting >6dB rises and falls
    min_freq_range = 300  # minimum frequency range width in Hz
    min_repetitions = 3  # minimum repetitions per second
    max_repetitions = 10  # maximum repetitions per second
    max_variation = 0.2  # 20% time variation
    max_peak_time_diff = 0.005  # maximum time difference for peak alignment

    # Compute the spectrogram
    D = librosa.amplitude_to_db(np.abs(librosa.stft(y)), ref=np.max)

    # Find events exceeding the threshold
    events = librosa.effects.split(y, top_db=threshold_db)

    # Dictionary to store frequency ranges, their occurrences, peak timings, and max amplitude rise
    freq_ranges = {}

    # Analyze events
    for i in range(len(events)):
        start, end = events[i]

        # Extract the portion of the spectrogram corresponding to the event
        event_spectrogram = D[:, start:end]

        # Compute the frequency range of interest
        freq_bins = librosa.core.fft_frequencies(sr=sr)

        # Check for frequency range width > 300Hz
        for j in range(len(freq_bins)):
            if j + min_freq_range < len(freq_bins):
                freq_range = freq_bins[j:j + min_freq_range]

                # Compute the sum of energy within the frequency range
                energy_sum = np.sum(event_spectrogram[j:j + min_freq_range, :])

                # Compute the duration of the event
                duration = librosa.get_duration(y=y[start:end], sr=sr)

                # Compute the interval between the start of consecutive events
                if i > 0 and len(events[i - 1]) > 1:
                    interval = start - events[i - 1][1]
                    interval_sec = interval / sr

                    # Check if the interval is within the specified range and variation
                    if min_repetitions <= 1 / interval_sec <= max_repetitions and interval_sec * max_variation > interval_sec:
                        # Add the frequency range, occurrences, peak timings, and max amplitude rise to the dictionary
                        if tuple(freq_range) not in freq_ranges:
                            freq_ranges[tuple(freq_range)] = {'occurrences': 1, 'peaks': [start], 'max_amplitude_rise': energy_sum}
                        else:
                            freq_ranges[tuple(freq_range)]['occurrences'] += 1
                            freq_ranges[tuple(freq_range)]['peaks'].append(start)
                            freq_ranges[tuple(freq_range)]['max_amplitude_rise'] = max(freq_ranges[tuple(freq_range)]['max_amplitude_rise'], energy_sum)

    # Print the highest Hz at which amplitude rise is >6dB for each frequency range
    for idx, (freq_range, data) in enumerate(freq_ranges.items()):
        min_freq = min(freq_range)
        max_freq = max(freq_range)
        print(f"RANGE {idx + 1} = {min_freq}Hz - {max_freq}Hz")

# Directory containing the WAV files
audio_dir = '/content/drive/MyDrive/audios'

# List all WAV files in the audio directory
wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

# Iterate through the list of WAV files
for wav_file in wav_files:
    current_wav_file = os.path.join(audio_dir, wav_file)
    print(f"Analyzing {wav_file}")
    analyze_audio_file(current_wav_file)
    print("")

import os
import numpy as np
import librosa
import librosa.display

def analyze_audio_file(file_path):
    # Load the audio file
    y, sr = librosa.load(file_path, sr=None)

    # Set parameters for analysis
    threshold_db = 6  # threshold for detecting >6dB rises and falls
    min_freq_range = 300  # minimum frequency range width in Hz
    min_repetitions = 3  # minimum repetitions per second
    max_repetitions = 10  # maximum repetitions per second
    max_variation = 0.2  # 20% time variation
    max_peak_time_diff = 0.005  # maximum time difference for peak alignment

    # Compute the spectrogram
    D = librosa.amplitude_to_db(np.abs(librosa.stft(y)), ref=np.max)

    # Find events exceeding the threshold
    events = librosa.effects.split(y, top_db=threshold_db)

    # Dictionary to store frequency ranges, their occurrences, peak timings, and max amplitude rise
    freq_ranges = {}

    # Analyze events
    for i in range(len(events)):
        start, end = events[i]

        # Extract the portion of the spectrogram corresponding to the event
        event_spectrogram = D[:, start:end]

        # Compute the frequency range of interest
        freq_bins = librosa.core.fft_frequencies(sr=sr)

        # Check for frequency range width > 300Hz
        for j in range(len(freq_bins)):
            if j + min_freq_range < len(freq_bins):
                freq_range = freq_bins[j:j + min_freq_range]

                # Compute the sum of energy within the frequency range
                energy_sum = np.sum(event_spectrogram[j:j + min_freq_range, :])

                # Compute the duration of the event
                duration = librosa.get_duration(y=y[start:end], sr=sr)

                # Compute the interval between the start of consecutive events
                if i > 0 and len(events[i - 1]) > 1:
                    interval = start - events[i - 1][1]
                    interval_sec = interval / sr

                    # Check if the interval is within the specified range and variation
                    if min_repetitions <= 1 / interval_sec <= max_repetitions and interval_sec * max_variation > interval_sec:
                        # Add the frequency range, occurrences, peak timings, and max amplitude rise to the dictionary
                        if tuple(freq_range) not in freq_ranges:
                            freq_ranges[tuple(freq_range)] = {'occurrences': 1, 'peaks': [start], 'max_amplitude_rise': energy_sum}
                        else:
                            freq_ranges[tuple(freq_range)]['occurrences'] += 1
                            freq_ranges[tuple(freq_range)]['peaks'].append(start)
                            freq_ranges[tuple(freq_range)]['max_amplitude_rise'] = max(freq_ranges[tuple(freq_range)]['max_amplitude_rise'], energy_sum)

    # Print the highest Hz at which amplitude rise is >6dB for each frequency range
    for idx, (freq_range, data) in enumerate(freq_ranges.items()):
        min_freq = min(freq_range)
        max_freq = max(freq_range)
        print(f"RANGE {idx + 1} = {min_freq}Hz - {max_freq}Hz")

# Directory containing the WAV files
audio_dir = '/content/drive/MyDrive/audios'

# List all WAV files in the audio directory
wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

# Iterate through the list of WAV files
for wav_file in wav_files:
    current_wav_file = os.path.join(audio_dir, wav_file)
    print(f"Analyzing {wav_file}")
    analyze_audio_file(current_wav_file)
    print("")

import os
import librosa
import librosa.display
import matplotlib.pyplot as plt

# Define the root directory
root_dir = '/content/drive/MyDrive/audios'

# Directory containing the WAV files
audio_dir = root_dir

# List all WAV files in the audio directory
wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

# Display the first few WAV file names
print("WAV Files:")
print(wav_files[:5])

# Now, you can iterate through the list of WAV files and process them as needed.
# Example code to load and process a WAV file:
for wav_file in wav_files:
    current_wav_file = os.path.join(audio_dir, wav_file)
    y, sr = librosa.load(current_wav_file)

    # Display the waveform
    plt.figure(figsize=(12, 4))
    librosa.display.waveshow(y, sr=sr)
    plt.title(f'Waveform - {wav_file}')
    plt.show()

    # Display the spectrogram
    D = librosa.amplitude_to_db(librosa.stft(y), ref=np.max)
    plt.figure(figsize=(12, 4))
    librosa.display.specshow(D, sr=sr, x_axis='time', y_axis='log')
    plt.colorbar(format='%+2.0f dB')
    plt.title(f'Spectrogram - {wav_file}')
    plt.show()

import os
import numpy as np
import librosa
import librosa.display

def analyze_audio_file(file_path):
    # Load the audio file
    y, sr = librosa.load(file_path, sr=None)

    # Set parameters for analysis
    threshold_db = 6  # threshold for detecting >6dB rises and falls
    min_freq_range = 300  # minimum frequency range width in Hz
    min_repetitions = 3  # minimum repetitions per second
    max_repetitions = 10  # maximum repetitions per second
    max_variation = 0.2  # 20% time variation

    # Compute the spectrogram
    D = librosa.amplitude_to_db(np.abs(librosa.stft(y)), ref=np.max)

    # Find events exceeding the threshold
    events = librosa.effects.split(y, top_db=threshold_db)

    # Dictionary to store frequency ranges, their occurrences, peak timings, and max amplitude rise
    freq_ranges = {}

    # Analyze events
    for i in range(len(events)):
        start, end = events[i]

        # Extract the portion of the spectrogram corresponding to the event
        event_spectrogram = D[:, start:end]

        # Compute the frequency range of interest
        freq_bins = librosa.core.fft_frequencies(sr=sr)

        # Check for frequency range width > 300Hz
        for j in range(len(freq_bins)):
            if j + min_freq_range < len(freq_bins):
                freq_range = freq_bins[j:j + min_freq_range]

                # Compute the sum of energy within the frequency range
                energy_sum = np.sum(event_spectrogram[j:j + min_freq_range, :])

                # Compute the duration of the event
                duration = librosa.get_duration(y=y[start:end], sr=sr)

                # Compute the interval between the start of consecutive events
                if i > 0 and len(events[i - 1]) > 1:
                    interval = start - events[i - 1][1]
                    interval_sec = interval / sr

                    # Check if the interval is within the specified range and variation
                    if min_repetitions <= 1 / interval_sec <= max_repetitions and interval_sec * max_variation > interval_sec:
                        print(f"Event {i}: Start={start}, End={end}, Duration={duration} seconds")
                        print(f"Frequency Range: {freq_range} Hz")
                        print(f"Energy Sum: {energy_sum}")
                        print(f"Interval between consecutive events: {interval_sec} seconds")

                        # Add the frequency range, occurrences, peak timings, and max amplitude rise to the dictionary
                        if tuple(freq_range) not in freq_ranges:
                            freq_ranges[tuple(freq_range)] = {'occurrences': 1, 'peaks': [start], 'max_amplitude_rise': energy_sum}
                        else:
                            freq_ranges[tuple(freq_range)]['occurrences'] += 1
                            freq_ranges[tuple(freq_range)]['peaks'].append(start)
                            freq_ranges[tuple(freq_range)]['max_amplitude_rise'] = max(freq_ranges[tuple(freq_range)]['max_amplitude_rise'], energy_sum)

    # Print the frequency ranges for each identified band
    for idx, (freq_range, data) in enumerate(freq_ranges.items()):
        min_freq = min(freq_range)
        max_freq = max(freq_range)
        print(f"RANGE {idx + 1} = {min_freq}Hz - {max_freq}Hz")
        print(f"Occurrences: {data['occurrences']}")
        print(f"Peaks: {data['peaks']}")
        print(f"Max Amplitude Rise: {data['max_amplitude_rise']}")
        print("")

    # Print the number of ranges identified
    print(f"Number of ranges identified: {len(freq_ranges)}")

# Directory containing the WAV files
audio_dir = '/content/drive/MyDrive/audios'

# List all WAV files in the audio directory
wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

# Iterate through the list of WAV files
for wav_file in wav_files:
    current_wav_file = os.path.join(audio_dir, wav_file)
    print(f"Analyzing {wav_file}")
    analyze_audio_file(current_wav_file)
    print("")

import os
import numpy as np
import librosa
import librosa.display

def analyze_audio_file(file_path):
    # Load the audio file
    y, sr = librosa.load(file_path, sr=None)

    # Set parameters for analysis
    threshold_db = 6  # threshold for detecting >6dB rises and falls
    min_freq_range = 300  # minimum frequency range width in Hz
    min_repetitions = 3  # minimum repetitions per second
    max_repetitions = 10  # maximum repetitions per second
    max_variation = 0.2  # 20% time variation
    max_peak_time_diff = 0.005  # maximum time difference for peak alignment

    # Compute the spectrogram
    D = librosa.amplitude_to_db(np.abs(librosa.stft(y)), ref=np.max)

    # Find events exceeding the threshold
    events = librosa.effects.split(y, top_db=threshold_db)

    # Dictionary to store frequency ranges, their occurrences, peak timings, and max amplitude rise
    freq_ranges = {}

    # Analyze events
    for i in range(len(events)):
        start, end = events[i]

        # Extract the portion of the spectrogram corresponding to the event
        event_spectrogram = D[:, start:end]

        # Compute the frequency range of interest
        freq_bins = librosa.core.fft_frequencies(sr=sr)

        # Check for frequency range width > 300Hz
        for j in range(len(freq_bins)):
            if j + min_freq_range < len(freq_bins):
                freq_range = freq_bins[j:j + min_freq_range]

                # Compute the sum of energy within the frequency range
                energy_sum = np.sum(event_spectrogram[j:j + min_freq_range, :])

                # Compute the duration of the event
                duration = librosa.get_duration(y=y[start:end], sr=sr)

                # Compute the interval between the start of consecutive events
                if i > 0 and len(events[i - 1]) > 1:
                    interval = start - events[i - 1][1]
                    interval_sec = interval / sr

                    # Check if the interval is within the specified range and variation
                    if min_repetitions <= 1 / interval_sec <= max_repetitions and interval_sec * max_variation > interval_sec:
                        print(f"Event {i}: Start={start}, End={end}, Duration={duration} seconds")
                        print(f"Frequency Range: {freq_range} Hz")
                        print(f"Energy Sum: {energy_sum}")
                        print(f"Interval between consecutive events: {interval_sec} seconds")

                        # Add the frequency range, occurrences, peak timings, and max amplitude rise to the dictionary
                        if tuple(freq_range) not in freq_ranges:
                            freq_ranges[tuple(freq_range)] = {'occurrences': 1, 'peaks': [start], 'max_amplitude_rise': energy_sum}
                        else:
                            freq_ranges[tuple(freq_range)]['occurrences'] += 1
                            freq_ranges[tuple(freq_range)]['peaks'].append(start)
                            freq_ranges[tuple(freq_range)]['max_amplitude_rise'] = max(freq_ranges[tuple(freq_range)]['max_amplitude_rise'], energy_sum)

    # Print the frequency ranges for each identified band
    for idx, (freq_range, data) in enumerate(freq_ranges.items()):
        min_freq = min(freq_range)
        max_freq = max(freq_range)
        print(f"RANGE {idx + 1} = {min_freq}Hz - {max_freq}Hz")
        print(f"Occurrences: {data['occurrences']}")
        print(f"Peaks: {data['peaks']}")
        print(f"Max Amplitude Rise: {data['max_amplitude_rise']}")

        # Compare peaks with other ranges
        for other_range, other_data in freq_ranges.items():
            if other_range != freq_range:
                overlap = any(np.isclose(data_peak, other_data_peak, atol=max_peak_time_diff * sr) for data_peak in data['peaks'] for other_data_peak in other_data['peaks'])
                print(f"Overlap with RANGE {other_range}: {'YES' if overlap else 'NO'}")

        print("")

    # Print the number of ranges identified
    print(f"Number of ranges identified: {len(freq_ranges)}")

# Directory containing the WAV files
audio_dir = '/content/drive/MyDrive/audios'

# List all WAV files in the audio directory
wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

# Iterate through the list of WAV files
for wav_file in wav_files:
    current_wav_file = os.path.join(audio_dir, wav_file)
    print(f"Analyzing {wav_file}")
    analyze_audio_file(current_wav_file)
    print("")

import os
import librosa
import librosa.display
import matplotlib.pyplot as plt

# Directory containing the WAV files
audio_dir = '/content/drive/MyDrive/audios'

# List all WAV files in the audio directory
wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

# Iterate through the list of WAV files
for wav_file in wav_files:
    # Construct the full file path
    current_wav_file = os.path.join(audio_dir, wav_file)

    # Load the audio file
    train_amp, train_sr = librosa.load(current_wav_file, sr=None)

    # Compute the spectrogram
    train_spec = librosa.stft(train_amp)
    train_db = librosa.amplitude_to_db(abs(train_spec))

    # Plot the spectrogram
    fig, ax = plt.subplots(nrows=2, ncols=1, figsize=(14, 12), sharex=True)
    img = librosa.display.specshow(train_db, sr=train_sr, x_axis='time', y_axis='hz', ax=ax[0], cmap='twilight')
    ax[0].set(title=f'Linear-frequency power spectrogram - {wav_file}')
    ax[0].label_outer()

    librosa.display.specshow(train_db, sr=train_sr, x_axis='time', y_axis='log', ax=ax[1], cmap='twilight')
    ax[1].set(title=f'Log-frequency power spectrogram - {wav_file}')
    ax[1].label_outer()
    fig.colorbar(img, ax=ax, format='%+2.f dB')

    # Show the plots
    plt.show()

import os
import librosa
import librosa.display
import matplotlib.pyplot as plt

# Directory containing the WAV files
audio_dir = '/content/drive/MyDrive/audios'

# List all WAV files in the audio directory
wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

# Iterate through the list of WAV files
for wav_file in wav_files:
    # Construct the full file path
    current_wav_file = os.path.join(audio_dir, wav_file)

    # Load the audio file
    train_amp, train_sr = librosa.load(current_wav_file, sr=None)

    # Compute the magnitude spectrogram
    train_spec = librosa.stft(train_amp)
    train_mag_spec = np.abs(train_spec)

    # Compute the spectrogram in decibel scale
    train_db = librosa.amplitude_to_db(train_mag_spec)

    # Extract the spectral centroids
    spectral_centroids = librosa.feature.spectral_centroid(S=train_mag_spec, sr=train_sr)[0]

    # Extract the time and frame indices
    frames = range(len(spectral_centroids))
    t = librosa.frames_to_time(frames)

    # Plot the spectrogram with spectral centroids
    plt.figure(figsize=(14, 6))
    librosa.display.specshow(train_db, sr=train_sr, x_axis='time', y_axis='log', cmap='twilight')

    plt.plot(t, spectral_centroids, color='yellow', label='Spectral Centroid')
    plt.title(f"Spectral Centroids for {wav_file}")
    plt.colorbar(format='%+2.0f dB')
    plt.legend(loc='upper right', fontsize=16, facecolor='gray')

    # Show the plot
    plt.show()

import os
import librosa
import librosa.display
import matplotlib.pyplot as plt

# Directory containing the WAV files
audio_dir = '/content/drive/MyDrive/audios'

# List all WAV files in the audio directory
wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

# Iterate through the list of WAV files
for wav_file in wav_files:
    # Construct the full file path
    current_wav_file = os.path.join(audio_dir, wav_file)

    # Load the audio file
    y, sr = librosa.load(current_wav_file, sr=None)

    # Compute the chromagram
    S = np.abs(librosa.stft(y, n_fft=4096))**2
    chroma = librosa.feature.chroma_stft(S=S, sr=sr)

    # Plot the chromagram
    fig, ax = plt.subplots()
    img = librosa.display.specshow(chroma, y_axis='chroma', x_axis='time', ax=ax)
    fig.colorbar(img, ax=ax)
    ax.set(title=f'Chromagram - {wav_file}')

    # Show the plot
    plt.show()

# Commented out IPython magic to ensure Python compatibility.
# %matplotlib inline
import seaborn as sns

# Define the root directory
root_dir = '/content/drive/MyDrive/audios'

# Directory containing the WAV files
audio_dir = root_dir

# List all WAV files in the audio directory
wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

# Initialize an empty DataFrame to store audio features
columns = ['filename', 'duration', 'sampling_rate']
tp_df = pd.DataFrame(columns=columns)

# Iterate through the list of WAV files
for wav_file in wav_files:
    current_wav_file = os.path.join(audio_dir, wav_file)

    # Load the audio file and extract features
    y, sr = librosa.load(current_wav_file)
    duration = librosa.get_duration(y=y, sr=sr)

    # Append the features to the DataFrame
    tp_df = tp_df.append({'filename': wav_file, 'duration': duration, 'sampling_rate': sr}, ignore_index=True)

# Explore the distribution of audio file durations
plt.figure(figsize=(12, 6))
sns.histplot(tp_df['duration'], bins=30, kde=True, color='navy')
plt.title('Distribution of Audio File Durations')
plt.xlabel('Duration (seconds)')
plt.ylabel('Count')
plt.show()

import os
import librosa
import numpy as np
from scipy.signal import find_peaks

def find_amplitude_patterns(audio_file_path, threshold_db=6, frequency_range=(300, 20000), interval_variation_threshold=0.2):
    # Load audio file
    y, sr = librosa.load(audio_file_path, sr=None)

    # Set parameters
    hop_length = 512  # Adjust as needed

    # Calculate spectrogram
    D = librosa.amplitude_to_db(np.abs(librosa.stft(y, hop_length=hop_length)), ref=np.max)

    # Find peaks above the threshold using scipy's find_peaks
    peaks, _ = find_peaks(D.max(axis=0), height=threshold_db)

    # Get frequencies corresponding to the peaks
    frequencies = librosa.fft_frequencies(sr=sr, n_fft=hop_length)[peaks]

    # Filter frequencies within the specified range
    valid_frequencies = frequencies[(frequencies >= frequency_range[0]) & (frequencies <= frequency_range[1])]

    # Check if there are valid frequencies
    if len(valid_frequencies) == 0:
        print(f"No valid frequencies found in the specified range for {audio_file_path}.")
        return

    # Check for repeating patterns
    pattern_intervals = np.diff(valid_frequencies)
    mean_interval = np.mean(pattern_intervals)
    variation_percentage = np.abs((pattern_intervals - mean_interval) / mean_interval)

    # Check conditions for repeating patterns
    if np.all(variation_percentage < interval_variation_threshold):
        print(f"Repeating patterns found in {audio_file_path}:")
        print("Frequencies:", valid_frequencies)
        print("Pattern Intervals:", pattern_intervals)
        print("Mean Interval:", mean_interval)
        print("Variation Percentage:", variation_percentage)

        # Additional logic to identify the lowest Hz range >300Hz wide with specified conditions
        for i in range(len(pattern_intervals)):
            # Assuming pattern occurs 3x-10x/second
            if 3 <= 1 / pattern_intervals[i] <= 10:
                print("Pattern occurs at a suitable rate.")

                # Identify lowest Hz range >300Hz wide
                if valid_frequencies[i] + 300 <= valid_frequencies[i + 1]:
                    print("Lowest Hz range >300Hz wide identified.")
                break
    else:
        print(f"No repeating patterns found in {audio_file_path}.")

# Directory containing the WAV files
audio_dir = '/content/drive/MyDrive/audios'

# List all WAV files in the audio directory
wav_files = [file for file in os.listdir(audio_dir) if file.endswith('.wav')]

# Iterate through the list of WAV files
for wav_file in wav_files:
    current_wav_file = os.path.join(audio_dir, wav_file)
    print(f"Analyzing {current_wav_file}")
    find_amplitude_patterns(current_wav_file)