from setuptools import setup, find_packages

# Read README.md for long description
with open("README.md", "r", encoding="utf-8") as fh:
  long_description = fh.read()

setup(
    name="minecraft-script",
    version="0.1.0",
    author="Nasir Idrishi",
    author_email="nasiridrishi@outlook.com",
    description="A kid-friendly Minecraft scripting library for learning Python",
    long_description=long_description,
    long_description_content_type="text/markdown",
    url="https://github.com/Riftal-Studios/ScriptyMc",
    project_urls={
      "Bug Tracker": "https://github.com/Riftal-Studios/ScriptyMc/issues",
      "Documentation": "https://github.com/Riftal-Studios/ScriptyMc/wiki",
    },
    classifiers=[
      "Programming Language :: Python :: 3",
      "Programming Language :: Python :: 3.8",
      "Programming Language :: Python :: 3.9",
      "Programming Language :: Python :: 3.10",
      "License :: OSI Approved :: MIT License",
      "Operating System :: OS Independent",
      "Development Status :: 3 - Alpha",
      "Intended Audience :: Education",
      "Topic :: Education",
      "Topic :: Games/Entertainment",
    ],
    package_dir={"": "src"},
    packages=find_packages(where="src"),
    python_requires=">=3.8",
    install_requires=[
      "requests>=2.25.1",
      "dataclasses;python_version<'3.7'",
    ],
    extras_require={
      'dev': [
        'pytest>=6.0',
        'pytest-cov>=2.0',
        'flake8>=3.9.0',
        'black>=21.0',
        'mypy>=0.900',
      ],
    }
)
