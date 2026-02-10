# iDempiere Hybrid Search

iDempiere Hybrid Search is a plugin that enhances iDempiere's search capabilities by combining traditional full-text (lexical) search with modern vector (semantic) search. This approach, known as Hybrid Search, provides more relevant results by understanding both the specific keywords and the semantic meaning of the search query.

![Hybrid Search Type](medias/Hybrid%20Search%20Type.png)

## Features

- **Hybrid Search Engine**: Combines keyword-based search (PostgreSQL TSVector / Oracle Text) with vector similarity search for superior relevance.
- **Multiple Database Support**:
    - **PostgreSQL**: Utilizes `pgvector` for vector storage and similarity search, and optionally `pgai` for embedding generation.
    - **Oracle**: Supports Oracle's native search and vector capabilities.
- **Embedding Services**:
    - **Local ONNX**: Generate embeddings locally using ONNX models without external API dependencies.
    - **Database-side**: Leverage database-native embedding capabilities (e.g., PostgreSQL `pgai`).
- **Flexible Configuration**: Define search indices and columns directly within iDempiere's metadata.
- **Automatic Indexing**: Event-based updates ensure your search index stays synchronized with record changes.
- **Reciprocal Rank Fusion (RRF)**: Intelligently merges and ranks results from both lexical and semantic searches.

## Architecture

The project is built on several key interfaces:
- `IHybridSearchProvider`: Handles the search execution and indexing logic for specific databases.
- `IEmbeddingService`: Provides a pluggable way to generate vector embeddings from text content.

## Setup and Configuration

### 1. Search Definition
Create a new **Search Definition** and set the **Search Type** to `HybridSearch`.

### 2. Search Columns
Define the columns you want to include in the search index. You can assign weights to different columns to influence the search results.

![Business Partner Search Column](medias/Business%20Partner%20Search%20Column.png)
*Example: Configuring columns for Business Partner search.*

### 3. Creating the Index
Once configured, run the **Create Index** process to populate the initial search index (HYS_SearchIndex) for existing records.

### 4. Update vector index
- **Update Hybrid Search Index**: process to create embedding vector (HYS_SearchIndex_Embedding) for records in the search index table (HYS_SearchIndex). Scheduled to run every 15 minutes.

## Database Schema

### HYS_SearchColumn
Searc column tab at the Search Definition window

### HYS_SearchIndex
Store the content generated from HYS_SearchColumn definitions, full text search vector (for PostgreSQL) and index status (Pending, Indexed or Error).

### HYS_SearchIndex_Embedding
Store the embedding vector for records in HYS_SearchIndex.

## Usage Examples

### Business Partner Search
The following video demonstrates the hybrid search in action for Business Partners, finding matches even when the exact terms don't match but the meaning is related.

[Business Partner Search Demo](medias/Business%20Partner%20Search%201080p.mp4)

### Menu Search
Hybrid search can also be applied to iDempiere menus, making it easier for users to find functionality using natural language.

![Menu Search Column](medias/Menu%20Search%20Column.png)
*Example: Configuring columns for Menu search.*

[Menu Search Demo](medias/Menu%20Search%201080p.mp4)

## Examples
Ready-to-use search configurations (2Pack) are provided in the `examples` folder:
- `BPartnerSearches.zip`: Search configuration for Business Partners.
- `MenuSearches.zip`: Search configuration for iDempiere Menus.

## Requirements
- iDempiere 13 or higher.
- For PostgreSQL: `pgvector` extension must be installed.
- For Local Embeddings: The plugin includes necessary ONNX libraries.

---
*Developed by [hengsin](https://github.com/hengsin)*
