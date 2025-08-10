import sys
import pandas as pd
import matplotlib.pyplot as plt

# Expect: python3 plot_generator.py <csv_file_path> <output_image_path>
if len(sys.argv) != 4:
    print("Usage: python3 plot_generator.py <csv_file_path> <output_image_path> <graph_title>")
    sys.exit(1)

csv_path = sys.argv[1]
output_path = sys.argv[2]
graph_title = sys.argv[3]

df = pd.read_csv(csv_path)
df.plot(x='Iteration', y='ExecutionTimeMs', kind='line')

# Extract metrics
avg_time = df["ExecutionTimeMs"].mean()
min_time = df["ExecutionTimeMs"].min()
max_time = df["ExecutionTimeMs"].max()

# Plot setup
plt.figure(figsize=(10, 6))
plt.plot(df["Iteration"], df["ExecutionTimeMs"], label="Execution Time")

# Annotate values
text = f"Avg: {avg_time:.4f} ms\nMin: {min_time:.4f} ms\nMax: {max_time:.4f} ms"
plt.text(
    0.95, 0.95, text,
    ha="right", va="top",
    transform=plt.gca().transAxes,
    fontsize=10,
    bbox=dict(boxstyle="round,pad=0.4", edgecolor="black", facecolor="lightyellow")
)

# Labels and formatting
plt.title("Benchmark "+graph_title)
plt.xlabel("Iteration")
plt.ylabel("Execution Time (ms)")
plt.grid(True)
plt.legend()
plt.tight_layout()
plt.savefig(output_path)

print(f"Plot with stats saved as {output_path}")